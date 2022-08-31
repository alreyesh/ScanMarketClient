package alreyesh.android.scanmarketclient.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.activities.MainActivity;
import alreyesh.android.scanmarketclient.notifications.NotificacionPush;
import alreyesh.android.scanmarketclient.notifications.NotificationHandler;
import alreyesh.android.scanmarketclient.utils.Util;
import alreyesh.android.scanmarketclient.work.Workmanagernoti;

public class Notifyservice extends Service {
    NotificationHandler notificacionHandler;
    ListenerRegistration registration;
    ListenerRegistration deletenoti;
    private SharedPreferences prefs;
    private SharedPreferences prefs1;
    private static final int ID_SERVICE = 101;
    private FirebaseFirestore db;
    //notificacion
    public static final String CHANNEL_HIGH_ID ="1";
    private static final String CHANNELHIGHNAME ="HIGH CHANNEL";
    public static final String CHANNEL_LOW_ID ="2";
    private static final String CHANNELLOWNAME ="LOW CHANNEL";
    private static final String CHANNEL_PUSH ="sevice_push";
    private NotificationManager manager;
    private Integer colorpick;
    @Override
    public void onCreate() {
        super.onCreate();
        prefs = Util.getSP(getApplication());
        Boolean turn = Util.getTurnNotify(prefs);
        createChannels();

        if(Boolean.TRUE.equals(turn)){
         /*   NotificacionPush p = new NotificacionPush();
            p.onNotiPause(getApplication());
            */
            onNotification(getApplication());

        }else{
            stopForeground(true);
            stopSelf();
        }



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





    public void onNotification(Context context){
        notificacionHandler = new NotificationHandler(context);
        db = FirebaseFirestore.getInstance();
        prefs = Util.getSP(context);
        prefs1= Util.getSP(context);
        registration=   db.collection("notificacion").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if( snapshot == null){
                    return;
                }
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }

                try{
                    for (DocumentChange dc : snapshot.getDocumentChanges()) {
                        SharedPreferences.Editor editor = prefs.edit();
                        Map<String, Object> o;
                        Boolean turn = Util.getTurnNotify(prefs1);
                        Boolean enviarnow;
                        switch (dc.getType()) {
                            case ADDED:

                                break;
                            case MODIFIED:
                                Log.d("TAG", "Modified city: " + dc.getDocument().getData());


                                 o = dc.getDocument().getData();
                                editor.putBoolean("turnnoti",false);
                                editor.putString("titlenoti",(String)o.get("titulo"));
                                editor.putString("shortnoti",(String)o.get("textocorto"));
                                editor.putString("descripnoti",(String)o.get("mensaje"));
                                editor.commit();
                                // Notify notify = o.toObject(Notify.class);
                                Log.d("d", "Current data: " +(String) o.get("titulo")  );

                                //sendNotification(notify.getTitulo(),notify.getTextocorto(),true);
                                Toast.makeText(context,"Esta: "+turn,Toast.LENGTH_SHORT).show();
                                enviarnow = (Boolean) o.get("sendnow");
                                if(Boolean.TRUE.equals(enviarnow) ){
                                    if(Boolean.TRUE.equals(turn)) {
                                        sendNotification((String) o.get("titulo"),(String)  o.get("textocorto"),true);

                                    }
                                }else{
                                    Long alertime = (Long) o.get("milisecond");
                                    Data data = guardarData((String)o.get("titulo"),(String)o.get("textocorto"),(String)o.get("mensaje"),(String)o.get("codigo"));
                                    Workmanagernoti.GuardarNoti(alertime,data,(String)o.get("codigo"),context);
                                }






                                break;
                            case REMOVED:
                                Log.d("TAG", "Removed city: " + dc.getDocument().getData());
                                break;
                        }
                    }
                }catch (NullPointerException n){

                }

            }
        });





    }
    private void sendNotification(String titulo ,String titulocorto,boolean isHighImportance ){
        Integer colorparse = Util.getPurchaseColor(prefs);
        colorpick = colorparse;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.putExtra("notfy", "on");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_PUSH);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setContentTitle(titulo)
                    .setContentText(titulocorto)
                    .setContentIntent(pendingNotificationIntent)
                    .setSmallIcon(R.mipmap.ic_logo_scan_market)
                    .setColor(colorpick)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.mipmap.ic_logo_scan_market))
                    .build();
            startForeground(ID_SERVICE, notification);
        }else{
            Notification.Builder nb  = notificacionHandler.createNotification(titulo,titulocorto,isHighImportance,"push");

            notificacionHandler.getManager().notify(1,nb.build());
        }





    }
    private Data guardarData(String titulo,String textocorto,String texto,String id_noti){
        return new Data.Builder()
                .putString("titulo",titulo)
                .putString("detalle",textocorto)
                .putString("texto",texto)
                .putString("idnoti",id_noti)
                .build();

    }
    public NotificationManager getManager() {
        if(manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
    private void createChannels(){
        if(Build.VERSION.SDK_INT >= 26){
            //Creatting  High channel
            NotificationChannel highChannel = new NotificationChannel(
                    CHANNEL_HIGH_ID, CHANNEL_PUSH,NotificationManager.IMPORTANCE_HIGH);
            //Extra Config
            highChannel.enableLights(true);
            highChannel.setLightColor(Color.RED);
            highChannel.setShowBadge(true);
            highChannel.enableVibration(true);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            highChannel.setSound(defaultSoundUri,null);

            highChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);


            getManager().createNotificationChannel(highChannel);




        }
    }
}

package alreyesh.android.scanmarketclient.work;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.test.InstrumentationRegistry.getContext;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.ForegroundInfo;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.TimeUnit;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.activities.MainActivity;
import alreyesh.android.scanmarketclient.notifications.NotificationHandler;
import alreyesh.android.scanmarketclient.utils.Util;

public class Workmanagernoti extends Worker {

    NotificationHandler notificacionHandler;
    Context context;
    private NotificationManager manager;
    public static final String CHANNEL_HIGH_Program_ID ="3";
    private static final String CHANNEL_PROGRAM_PUSH ="sevice_program_push";
    private NotificationManager notificationManager;
    private SharedPreferences prefs;
    private Integer colorpick;
    Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    public Workmanagernoti(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        prefs = Util.getSP(context);
        notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
    }
    public static void GuardarNoti(Long duracion, Data data,String tag,Context context ){
        Toast.makeText(context,"Duracion: "+duracion,Toast.LENGTH_SHORT).show();
        //   .setInitialDelay(duracion, TimeUnit.MILLISECONDS).addTag(tag)
        //   .setInputData(data).build();
      OneTimeWorkRequest noti = new OneTimeWorkRequest.Builder(Workmanagernoti.class)
                .setInitialDelay(10, TimeUnit.SECONDS).addTag(tag).setInputData(data).build();;
        WorkManager.getInstance( ).enqueue(noti);





    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("worknotifi","llego");
       String titulo = getInputData().getString("titulo");
        String detalle = getInputData().getString("detalle");




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setForegroundAsync(createForegroundInfo("titulo","detalle"));
        }else{
            sendNotification(titulo,detalle,true);

        }





        return Result.success();
    }

    @NonNull
    @Override
    public ListenableFuture<ForegroundInfo> getForegroundInfoAsync() {

        Toast.makeText(context,"Prueba notificacion",Toast.LENGTH_LONG).show();


        return super.getForegroundInfoAsync();
    }

    private void sendNotification(String titulo, String detalle, boolean isHighImportance) {
        Notification.Builder nb  = notificacionHandler.createNotification(titulo,detalle,isHighImportance,"push");

        notificacionHandler.getManager().notify(1,nb.build());
    }

    private ForegroundInfo createForegroundInfo(  String titulo,String titulocorto ){
         Context context = getApplicationContext();
         PendingIntent intent = WorkManager.getInstance(context)
                 .createCancelPendingIntent(getId());
         Integer colorparse = Util.getPurchaseColor(prefs);
         colorpick = colorparse;
         Notification notification;
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             createChannel();
             Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
             notificationIntent.putExtra("notfy", "on");
             notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
             PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
             NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_PROGRAM_PUSH);
            notification = notificationBuilder.setOngoing(true)
                     .setContentTitle(titulo)
                     .setContentText(titulocorto)
                     .setContentIntent(pendingNotificationIntent)
                     .setSmallIcon(R.mipmap.ic_logo_scan_market)
                     .setColor(colorpick)
                     .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.mipmap.ic_logo_scan_market))
                     .build();
         }else{
             Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
             notificationIntent.putExtra("notfy", "on");
             notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
             PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
             Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
             NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
            notification = notificationBuilder.setOngoing(true)
                     .setContentTitle(titulo)
                     .setContentText(titulocorto)
                     .setContentIntent(pendingNotificationIntent)
                     .setSmallIcon(R.mipmap.ic_logo_scan_market)
                     .setColor(colorpick)
                     .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.mipmap.ic_logo_scan_market))
                     .build();

         }
         return new ForegroundInfo(10,notification);

     }
    public NotificationManager getManager() {
        if(manager == null){
            manager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
    private void createChannel() {

        if(Build.VERSION.SDK_INT >= 26){
            //Creatting  High channel
            NotificationChannel highChannel = new NotificationChannel(
                    CHANNEL_HIGH_Program_ID, CHANNEL_PROGRAM_PUSH,NotificationManager.IMPORTANCE_HIGH);
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

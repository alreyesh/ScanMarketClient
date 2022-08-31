package alreyesh.android.scanmarketclient.notifications;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.work.Data;
import androidx.work.WorkManager;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import alreyesh.android.scanmarketclient.app.MyApp;
import alreyesh.android.scanmarketclient.dialog.AddListPurchaseDialog;
import alreyesh.android.scanmarketclient.dialog.NotificationDialog;
import alreyesh.android.scanmarketclient.model.Notify;
import alreyesh.android.scanmarketclient.utils.Util;
import alreyesh.android.scanmarketclient.work.Workmanagernoti;

public class NotificacionPush {
    private FirebaseFirestore db;
    NotificationHandler notificacionHandler;
    ListenerRegistration registration;
    ListenerRegistration deletenoti;
    private SharedPreferences prefs;
    private SharedPreferences prefs1;
    public void noty(Context context){
       // Toast.makeText(context,"Hola esto es una prueba",Toast.LENGTH_LONG).show();

    }
    public void onNotiPause(Context context){
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
      Notification.Builder nb  = notificacionHandler.createNotification(titulo,titulocorto,isHighImportance,"push");

        notificacionHandler.getManager().notify(1,nb.build());


    }

    private void eliminarnoti(Context context,String tag){
        WorkManager.getInstance(context).cancelAllWorkByTag(tag);
    }
    private Data guardarData(String titulo,String textocorto,String texto,String id_noti){
        return new Data.Builder()
                .putString("titulo",titulo)
                .putString("detalle",textocorto)
                .putString("texto",texto)
                .putString("idnoti",id_noti)
                .build();

    }

}

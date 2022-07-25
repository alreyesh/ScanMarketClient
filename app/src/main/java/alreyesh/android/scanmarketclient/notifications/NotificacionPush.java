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

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import alreyesh.android.scanmarketclient.app.MyApp;
import alreyesh.android.scanmarketclient.dialog.AddListPurchaseDialog;
import alreyesh.android.scanmarketclient.dialog.NotificationDialog;
import alreyesh.android.scanmarketclient.model.Notify;
import alreyesh.android.scanmarketclient.utils.Util;

public class NotificacionPush {
    private FirebaseFirestore db;
    NotificationHandler notificacionHandler;
    private SharedPreferences prefs;
    public void noty(Context context){
       // Toast.makeText(context,"Hola esto es una prueba",Toast.LENGTH_LONG).show();

    }
    public void onNotiPause(Context context){
        notificacionHandler = new NotificationHandler(context);
        db = FirebaseFirestore.getInstance();
        prefs = Util.getSP(context);
        SharedPreferences.Editor editor = prefs.edit();
       db.collection("notificacion").addSnapshotListener(new EventListener<QuerySnapshot>() {
           @Override
           public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
               if (e != null) {
                   Log.w("TAG", "listen:error", e);
                   return;
               }

               for (DocumentChange dc : snapshot.getDocumentChanges()) {
                   switch (dc.getType()) {
                       case ADDED:
                           Log.d("TAG", "New city: " + dc.getDocument().getData());
                           break;
                       case MODIFIED:
                           Log.d("TAG", "Modified city: " + dc.getDocument().getData());
                           Map<String, Object> o = dc.getDocument().getData();
                           editor.putString("titlenoti",(String)o.get("titulo"));
                           editor.putString("shortnoti",(String)o.get("textocorto"));
                           editor.putString("descripnoti",(String)o.get("mensaje"));
                           editor.commit();
                           // Notify notify = o.toObject(Notify.class);
                           Log.d("d", "Current data: " +(String) o.get("titulo")  );

                          //sendNotification(notify.getTitulo(),notify.getTextocorto(),true);
                           sendNotification((String) o.get("titulo"),(String)  o.get("textocorto"),true);
                           break;
                       case REMOVED:
                           Log.d("TAG", "Removed city: " + dc.getDocument().getData());
                           break;
                   }
               }
           }
       });
    }

    public void onNotiResume(Context context ){

        prefs = Util.getSP(context);
        notificacionHandler = new NotificationHandler(context);
        SharedPreferences.Editor editor = prefs.edit();
        db = FirebaseFirestore.getInstance();

        db.collection("notificacion").addSnapshotListener(new EventListener<QuerySnapshot>() {


            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }

                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d("TAG", "New city: " + dc.getDocument().getData());
                            break;
                        case MODIFIED:
                            Log.d("TAG", "Modified city: " + dc.getDocument().getData());
                            Map<String, Object> o = dc.getDocument().getData();
                            editor.putString("titlenoti",(String)o.get("titulo"));
                            editor.putString("shortnoti",(String)o.get("textocorto"));
                            editor.putString("descripnoti",(String)o.get("mensaje"));
                            editor.commit();
                            Log.d("d", "Current data: " +(String) o.get("titulo")  );
                            NotificationDialog notificacion = new NotificationDialog();
                            FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                            notificacion.show(fragmentManager,"notificacionview");



                            break;
                        case REMOVED:
                            Log.d("TAG", "Removed city: " + dc.getDocument().getData());
                            break;
                    }
                }

            }
        });




    }

    private void sendNotification(String titulo ,String titulocorto,boolean isHighImportance ){
        Notification.Builder nb  = notificacionHandler.createNotification(titulo,titulocorto,isHighImportance,"push");

        notificacionHandler.getManager().notify(1,nb.build());


    }

}

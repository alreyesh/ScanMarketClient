package alreyesh.android.scanmarketclient.Notifications;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import alreyesh.android.scanmarketclient.Activities.MainActivity;
import alreyesh.android.scanmarketclient.Fragments.CartFragment;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.Utils.Util;

public class NotificationHandler  extends ContextWrapper {
    private NotificationManager manager;
    private SharedPreferences prefs;
    public static final String CHANNEL_HIGH_ID ="1";
    private  final String CHANNEL_HIGH_NAME ="HIGH CHANNEL";
    public static final String CHANNEL_LOW_ID ="2";
    private  final String CHANNEL_LOW_NAME ="LOW CHANNEL";
    public NotificationHandler(Context context) {
        super(context);
        createChannels();
    }

    public NotificationManager getManager() {
        if(manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
    //config
    private Integer colorpick;
    private void createChannels(){
        if(Build.VERSION.SDK_INT >= 26){
            //Creatting  High channel
            NotificationChannel highChannel = new NotificationChannel(
                    CHANNEL_HIGH_ID,CHANNEL_HIGH_NAME,NotificationManager.IMPORTANCE_HIGH);
            //Extra Config
            highChannel.enableLights(true);
            highChannel.setLightColor(Color.RED);
            highChannel.setShowBadge(true);
            highChannel.enableVibration(true);
          //  highChannel.setVibrationPattern(new long[] {100,200,300,400,500,400,300,200,400});
           Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            highChannel.setSound(defaultSoundUri,null);

            highChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            //Creating Low channel
            NotificationChannel lowChannel = new NotificationChannel(
                    CHANNEL_LOW_ID,CHANNEL_LOW_NAME,NotificationManager.IMPORTANCE_LOW);
            lowChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            lowChannel.setSound(defaultSoundUri,null);
            getManager().createNotificationChannel(highChannel);
            getManager().createNotificationChannel(lowChannel);



        }else{

        }
    }

    public Notification.Builder createNotification(String title,String message, boolean isHighImportant){
        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        Integer colorparse = Util.getPurchaseColor(prefs);
        colorpick = colorparse;
        if(colorpick ==null){
            colorpick = getColor(R.color.colorPrimary);
        }
        if(Build.VERSION.SDK_INT >= 26){
                if(isHighImportant){
                    return this.createNotificationWithChannel(title,message, CHANNEL_HIGH_ID);

                }
                return this.createNotificationWithChannel(title,message, CHANNEL_LOW_ID);

            }
        return this.createNotificationWithoutChannel(title,message);
    }
    private Notification.Builder createNotificationWithChannel(String title,String message, String channelId){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.putExtra("showCartView", true);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                  return new Notification.Builder(getApplicationContext(), channelId)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setColor(colorpick)
                          .setContentIntent(pendingNotificationIntent)
                        .setSmallIcon(R.mipmap.ic_logo_scan_market)
                        .setAutoCancel(true);

            }
        return null;
    }
    private Notification.Builder createNotificationWithoutChannel(String title,String message){
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.putExtra("showCartView", true);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        return new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingNotificationIntent)
                .setColor(colorpick)
                .setSmallIcon(R.mipmap.ic_logo_scan_market)
                .setAutoCancel(true);
    }


}

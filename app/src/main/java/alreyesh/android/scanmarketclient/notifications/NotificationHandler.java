package alreyesh.android.scanmarketclient.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import alreyesh.android.scanmarketclient.activities.MainActivity;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.utils.Util;

public class NotificationHandler  extends ContextWrapper {
    private NotificationManager manager;
    private SharedPreferences prefs;
    public static final String CHANNEL_HIGH_ID ="1";
    private static final String CHANNELHIGHNAME ="HIGH CHANNEL";
    public static final String CHANNEL_LOW_ID ="2";
    private static final String CHANNELLOWNAME ="LOW CHANNEL";
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
                    CHANNEL_HIGH_ID, CHANNELHIGHNAME,NotificationManager.IMPORTANCE_HIGH);
            //Extra Config
            highChannel.enableLights(true);
            highChannel.setLightColor(Color.RED);
            highChannel.setShowBadge(true);
            highChannel.enableVibration(true);

           Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            highChannel.setSound(defaultSoundUri,null);

            highChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            //Creating Low channel
            NotificationChannel lowChannel = new NotificationChannel(
                    CHANNEL_LOW_ID, CHANNELLOWNAME,NotificationManager.IMPORTANCE_LOW);
            lowChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            lowChannel.setSound(defaultSoundUri,null);
            getManager().createNotificationChannel(highChannel);
            getManager().createNotificationChannel(lowChannel);



        }else{

        }
    }

    public Notification.Builder createNotification(String title,String message, boolean isHighImportant,String type){
        prefs = Util.getSP(getApplicationContext());
        Integer colorparse = Util.getPurchaseColor(prefs);
        colorpick = colorparse;
        if(colorpick ==null){
            colorpick = getColor(R.color.colorPrimary);
        }
        if(type == "alarma"){
            if(Build.VERSION.SDK_INT >= 26){
                if(isHighImportant){
                    return this.createNotificationWithChannel(title,message, CHANNEL_HIGH_ID);

                }
                return this.createNotificationWithChannel(title,message, CHANNEL_LOW_ID);

            }
            return this.createNotificationWithoutChannel(title,message);
        }else if(type == "push"){
            if(Build.VERSION.SDK_INT >= 26){

                return this.createPushWithChannel(title,message,CHANNEL_HIGH_ID);

            }

            return this.createPushWithoutChannel(title,message);

        }

       return null;

    }
    //Alerta
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
                          .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.mipmap.ic_logo_scan_market))

                          .setAutoCancel(true);

            }
        return null;
    }
    private Notification.Builder createNotificationWithoutChannel(String title,String message){


        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.putExtra("showCartView", true);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        return new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingNotificationIntent)
                .setSound(defaultSoundUri)
                .setColor(colorpick)
                .setSmallIcon(R.mipmap.ic_logo_scan_market)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.mipmap.ic_logo_scan_market))
                .setAutoCancel(true);
    }

    //Push

    private Notification.Builder createPushWithChannel(String title,String message, String channelId){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.putExtra("notfy", "on");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            return new Notification.Builder(getApplicationContext(), channelId)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setColor(colorpick)
                    .setContentIntent(pendingNotificationIntent)
                    .setSmallIcon(R.mipmap.ic_logo_scan_market)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.mipmap.ic_logo_scan_market))

                    .setAutoCancel(true);

        }
        return null;
    }

    private Notification.Builder createPushWithoutChannel(String title,String message){


        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.putExtra("notfy", "on");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        return new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingNotificationIntent)
                .setSound(defaultSoundUri)
                .setColor(colorpick)
                .setSmallIcon(R.mipmap.ic_logo_scan_market)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.mipmap.ic_logo_scan_market))
                .setAutoCancel(true);
    }


}

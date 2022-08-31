package alreyesh.android.scanmarketclient.work

import alreyesh.android.scanmarketclient.R
import alreyesh.android.scanmarketclient.activities.MainActivity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters

class MyWorker(val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "11"
        private const val NOTIFICATION_CHANNEL_NAME = "Work Service"
        var PENDING_INTENT_FLAG_MUTABLE=0;
    }

    override suspend fun doWork(): Result {

        return Result.success();
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            PENDING_INTENT_FLAG_MUTABLE =if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java),  PENDING_INTENT_FLAG_MUTABLE))
                .setSmallIcon(R.mipmap.ic_logo_scan_market)
                .setOngoing(true)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle(context.getString(R.string.app_name))
                .setLocalOnly(true)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setContentText("Updating widget")
                .build()
            return ForegroundInfo(1337, notification)
        }else{
            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java),  PENDING_INTENT_FLAG_MUTABLE))
                .setSmallIcon(R.mipmap.ic_logo_scan_market)
                .setOngoing(true)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle(context.getString(R.string.app_name))
                .setLocalOnly(true)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setContentText("Updating widget")
                .build()
            return ForegroundInfo(1338, notification)
        }


    }



}
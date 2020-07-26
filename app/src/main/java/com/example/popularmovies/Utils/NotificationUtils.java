package com.example.popularmovies.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.popularmovies.MainActivity;
import com.example.popularmovies.R;

public class NotificationUtils {

    private static final String NETWORK_CHANNEL_ID = "NETWORK_NOTIFICATIONS";
    private static final int NETWORK_NOTIF_ID = 8765;

    /**
     * notify user that we have cached movie data from the web
     * @param context used to get acccess to utility/system methods
     */
    public static void notifyUserOfMoviesUpdated(Context context) {
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, NETWORK_CHANNEL_ID);

        //Create intent to open app when notification shown
        Intent openAppIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(openAppIntent);

        PendingIntent resultPendingIntent = taskStackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //build our notification
        Notification new_data_notif = notifBuilder
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.notification_network_title))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_sync_black_40dp)
                .setContentIntent(resultPendingIntent)
                .build();

        //notify manager of notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NETWORK_NOTIF_ID, new_data_notif);

    }

    public static void createNotificationChannel(Context context){
        CharSequence name = context.getString(R.string.notification_channel_name);
        String desc = context.getString(R.string.notification_channel_desc);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(NETWORK_CHANNEL_ID, name, importance);
        channel.setDescription(desc);
        //register with system. NOTE: can't change importance or behavior after this
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}

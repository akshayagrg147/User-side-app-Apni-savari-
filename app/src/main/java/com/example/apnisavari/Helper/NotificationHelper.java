package com.example.apnisavari.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.apnisavari.R;

public class NotificationHelper extends ContextWrapper {
    private static final String Edmt_channel_id = "com.example.apnisavari";
    private static final String edmt_channel_name = "APNISAVARI";
    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        createChannels();


    }

   private void createChannels() {
        NotificationChannel edmtChannel=new NotificationChannel(Edmt_channel_id,
                edmt_channel_name,
                NotificationManager.IMPORTANCE_DEFAULT);
        edmtChannel.enableLights(true);
        edmtChannel.enableVibration(true);
        edmtChannel.setLightColor(Color.GRAY);
        edmtChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(edmtChannel);

    }

 public  NotificationManager getManager()
 {
     if(manager==null)
     {
         manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

     }
     return  manager;

}
@RequiresApi(api=Build.VERSION_CODES.O)
public  Notification.Builder getMyRideNotification(String title, String content, PendingIntent contentIntent
, Uri soundUri)
{
    return new Notification.Builder(getApplicationContext(),Edmt_channel_id)
            .setContentText(content)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.ic_directions_bike_black_24dp);
}
}
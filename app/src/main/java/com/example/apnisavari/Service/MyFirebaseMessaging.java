package com.example.apnisavari.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.apnisavari.Common.Common;
import com.example.apnisavari.Helper.NotificationHelper;
import com.example.apnisavari.Map_destination_Select;
import com.example.apnisavari.Model.Rider;
import com.example.apnisavari.R;
import com.example.apnisavari.RateActivity;
import com.example.apnisavari.acceptingcustomerwindow;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    SharedPreferences sharedPreferences;
    public  static final String MyPreference="DriverInfo";
    public static final String driverId="driverId";
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        sharedPreferences = getSharedPreferences(MyPreference, Context.MODE_PRIVATE);


        if (remoteMessage.getData() != null) {
            Map<String, String> data = remoteMessage.getData();
            String title = data.get("title");
            final String message = data.get("message");
            final String res = data.get("cancel");
            final String driverid = data.get("DriverId");

            Common.res = res;
            if (title.equals("Cancel")) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyFirebaseMessaging.this, "" + message, Toast.LENGTH_SHORT).show();

                    }
                });

                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getBaseContext());
                Intent localIntent = new Intent(Common.broad_cast_string);
                localBroadcastManager.sendBroadcast(localIntent);


            }
            if (title.equals("DriverCancelled")) {

                FirebaseDatabase.getInstance().getReference(Common.SwitchingSystem).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("switchingSystem").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                    }
                });

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                    Intent intent = new Intent(getBaseContext(), Map_destination_Select.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessaging.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    try {
                        // Perform the operation associated with our pendingIntent
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                } else {

                    Intent intent = new Intent(getBaseContext(),  Map_destination_Select.class);


                    startActivity(intent);


                }


                //  if (mcurrent != null)


            }


            if (title.equals("Arrived")) {

                //showArrivedNotification(remoteMessage.getNotification().getBody()); VERSION 26 LESS
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    showArrivedNotificationApi26(message);
                } else
                    showArrivedNotification(message);

            }
            if (title.equals("Accept")) {


                //showArrivedNotification(remoteMessage.getNotification().getBody()); VERSION 26 LESS


             //   SharedPreferences.Editor editor = sharedPreferences.edit();
             //   editor.putString(driverId, driverid);
            //    editor.apply();
                //     DatabaseReference dbrequest = FirebaseDatabase.getInstance().getReference(Common.SwitchingSystem);


                DatabaseReference dbrequest = FirebaseDatabase.getInstance().getReference(Common.SwitchingSystem);
                Rider ob = new Rider();
                ob.setSwitchingSystem(driverid);
                dbrequest.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(ob).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                    }
                });


                //   openRateSharedWindow(message);

            }



try {
    if (title.equals("DropOff")) {

        //showArrivedNotification(remoteMessage.getNotification().getBody()); VERSION 26 LESS
        openRateSharedWindow(message);


    }
} catch (Exception e) {
    e.printStackTrace();
}
        }
    }

    private void openRateSharedWindow(String body) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            LocalBroadcastManager.getInstance(MyFirebaseMessaging.this).sendBroadcast(new Intent(Common.broad_cast_dropoff));

            Intent intent = new Intent(this, RateActivity.class);
            //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                // Perform the operation associated with our pendingIntent
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            startActivity(intent);


        }
        else
        {
            Intent intent = new Intent(this, RateActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        }






    }
    private void openRateActivity(String body) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            LocalBroadcastManager.getInstance(MyFirebaseMessaging.this).sendBroadcast(new Intent(Common.broad_cast_dropoff));

            Intent intent = new Intent(this, RateActivity.class);
            //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                // Perform the operation associated with our pendingIntent
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            startActivity(intent);


        }
        else
        {
            Intent intent = new Intent(this, acceptingcustomerwindow.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


        }
    }

    private void showArrivedNotificationApi26(String body) {

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0,
                new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper=new NotificationHelper(getBaseContext());
        Notification.Builder builder=notificationHelper.getMyRideNotification("Arrived",body,contentIntent,defaultSound);
        notificationHelper.getManager().notify(1,builder.build());

    }

    private void showArrivedNotification(String body) {
       PendingIntent contentIntent=PendingIntent.getActivities(getBaseContext(),
                0,new Intent[]{},PendingIntent.FLAG_ONE_SHOT);
        Notification.Builder builder=new Notification.Builder(getBaseContext());
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_directions_bike_black_24dp)
                .setContentTitle("Arrived")
                .setContentText(body)
                .setContentIntent(contentIntent);
        NotificationManager manager=(NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,builder.build());


    }
}

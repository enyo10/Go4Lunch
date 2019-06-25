package ch.enyoholali.openclassrooms.go4lunch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.controllers.activities.PlaceDetailsActivity;

public class NotificationAlarmReceiver extends BroadcastReceiver {
    private static final String TAG=NotificationAlarmReceiver.class.getSimpleName();

    private Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext=context;
        String message =intent.getStringExtra(PlaceDetailsActivity.RESTAURANT_NAME);

        sendNotification(message);

    }

    public void sendNotification(String message){
        Log.d(TAG,"Notification Send");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "Chanel_id")
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle(" Lunch Time ")
                .setContentText(" You have plan to lunch in "+message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());

        Log.i("TAG","Notifications send");

    }
}

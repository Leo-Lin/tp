package taoyuan.taipower;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;

public class NotificationActivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_activty);


        Intent intent = new Intent(this, ExampleActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Content title") //標題
                .setContentText("Content text") //內文

                .setTicker("Ticker") //跳出來時在通知bar顯示的文字

                .setContentIntent(pendingIntent) //設定點通知後要開啟的activity

                .setAutoCancel(true)

                .setSmallIcon(R.drawable.ic_add_white_24dp)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
        notificationManager.notify(1, notification);
        notificationManager.notify(2, notification);

        notificationManager.cancel(0);
    }
}

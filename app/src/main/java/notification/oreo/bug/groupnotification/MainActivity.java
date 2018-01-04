package notification.oreo.bug.groupnotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "NotifBug";
    private static final int NOTIF_ID = 1;
    private static final String NOTIF_TAG_1 = "chat1";
    private static final String NOTIF_TAG_2 = "chat2";
    private static final String CHANNEL_ID = "channel_1";
    private static final String GROUP_ID = "group_id";
    private final Handler handler = new Handler();

    private NotificationManagerCompat notificationManagerCompat;
    private Context appContext;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        notificationManagerCompat = NotificationManagerCompat.from(appContext);

        setupChannel();
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button2);
    }

    public void triggerBug(View v) {
        button.setEnabled(false);
        cancelNotification();
        postNotification(true, false);
        textView.setText("Posted notification with summary and first child notification. Waiting for 10 seconds.");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                postNotification(false, true);
                textView.setText("Posted second notification with summary and second child notification. See the first notification peeking on Oreo.");
                button.setEnabled(true);
            }
        }, 10_000L);

    }

    private void setupChannel() {
        if (Build.VERSION.SDK_INT >=26) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Default channel", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void cancelNotification() {
        Log.i(LOG_TAG, "Cancel any existing notification");
        notificationManagerCompat.cancel(NOTIF_ID);
    }

    private void postNotification(boolean postChild1, boolean postChild2) {
        Intent intent = new Intent("intent_action").setClassName(appContext, MainActivity.class.getName());

        NotificationCompat.Builder summaryNotificationBuilder = new NotificationCompat.Builder(appContext, CHANNEL_ID);
        summaryNotificationBuilder.setContentTitle("Notif 1 summary title");
        summaryNotificationBuilder.setContentText("Notif 1 summary text");
        summaryNotificationBuilder.setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY);
        summaryNotificationBuilder.setGroupSummary(true);
        summaryNotificationBuilder.setGroup(GROUP_ID);
        summaryNotificationBuilder.setAutoCancel(true);
        summaryNotificationBuilder.setContentIntent(PendingIntent.getActivity(appContext, 0, intent, 0));
        summaryNotificationBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        summaryNotificationBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
        summaryNotificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        summaryNotificationBuilder.setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationCompat.Builder childNotification1 = new NotificationCompat.Builder(appContext, CHANNEL_ID);
        childNotification1.setContentTitle("Notif 1 child one content title");
        childNotification1.setContentText("Notif 1 child one content text");
        childNotification1.setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY);
        childNotification1.setGroupSummary(false);
        childNotification1.setGroup(GROUP_ID);
        childNotification1.setAutoCancel(true);
        childNotification1.setContentIntent(PendingIntent.getActivity(appContext, 0, intent, 0));
        childNotification1.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        childNotification1.setSmallIcon(R.drawable.ic_launcher_foreground);
        childNotification1.setPriority(NotificationCompat.PRIORITY_HIGH);
        childNotification1.setDefaults(NotificationCompat.DEFAULT_ALL);
        final NotificationCompat.MessagingStyle messagingStyle1 = new NotificationCompat.MessagingStyle("Contact 1");
        messagingStyle1.setConversationTitle("Conversation 1");
        messagingStyle1.addMessage("Message one fron contact1", System.currentTimeMillis(), "Contact 1");
        messagingStyle1.addMessage("Message two fron contact1", System.currentTimeMillis(), "Contact 1");
        childNotification1.setStyle(messagingStyle1);

        NotificationCompat.Builder childNotification2 = new NotificationCompat.Builder(appContext, CHANNEL_ID);
        childNotification2.setContentTitle("Notif 2 child two content title");
        childNotification2.setContentText("Notif 2 child two content text");
        childNotification2.setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY);
        childNotification2.setGroupSummary(false);
        childNotification2.setGroup(GROUP_ID);
        childNotification2.setAutoCancel(true);
        childNotification2.setContentIntent(PendingIntent.getActivity(appContext, 0, intent, 0));
        childNotification2.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        childNotification2.setSmallIcon(R.drawable.ic_launcher_foreground);
        childNotification2.setPriority(NotificationCompat.PRIORITY_HIGH);
        childNotification2.setDefaults(NotificationCompat.DEFAULT_ALL);
        final NotificationCompat.MessagingStyle messagingStyle2 = new NotificationCompat.MessagingStyle("Contact 2");
        messagingStyle2.setConversationTitle("Conversation 2");
        messagingStyle2.addMessage("Message one fron contact2", System.currentTimeMillis(), "Contact 2");
        messagingStyle2.addMessage("Message two fron contact2", System.currentTimeMillis(), "Contact 2");
        childNotification2.setStyle(messagingStyle2);

        if (postChild1) {
            Notification notif = childNotification1.build();
            Log.i(LOG_TAG, "Posting child 1 notification:" + notif);
            notificationManagerCompat.notify(NOTIF_TAG_1, NOTIF_ID, notif);
        }

        if (postChild2) {
            Notification notif = childNotification2.build();
            Log.i(LOG_TAG, "Posting child 2 notification:" + notif);
            notificationManagerCompat.notify(NOTIF_TAG_2, NOTIF_ID, notif);
        }

        Notification notification = summaryNotificationBuilder.build();
        Log.i(LOG_TAG, "Posting summary notification:" + notification);
        notificationManagerCompat.notify(NOTIF_ID, notification);
    }
}

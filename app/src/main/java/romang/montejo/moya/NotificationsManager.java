package romang.montejo.moya;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Random;

import romang.montejo.moya.Model.AudioReminder;
import romang.montejo.moya.Model.PhotoReminder;
import romang.montejo.moya.Model.Reminder;
import romang.montejo.moya.Model.TextReminder;
import romang.montejo.moya.Persistence.StorageManager;
import romang.montejo.moya.Util.ParcelableUtil;

public class NotificationsManager {
    public static NotificationsManager instance;

    public Context context;
    public NotificationCompat.Builder nBuilder;
    public AlarmManager alarmManager;


    private int importance = NotificationManager.IMPORTANCE_DEFAULT;
    static private String CHANNEL_NAME = "romang.montejo.moya";
    static public String CHANNEL_ID;

    //types
    private static final int TEXT_TYPE = 1;
    private static final int IMG_TYPE = 2;
    private static final int AUD_TYPE = 3;

    public NotificationsManager(Context ctx) {
        context = ctx;
        CHANNEL_ID = String.valueOf(ctx.hashCode());
        startingChannel();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    static public NotificationsManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new NotificationsManager(ctx);
        }
        return instance;
    }

    public void startingChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void lauchNotification(Reminder reminder) {
        Intent intent = new Intent(instance.context, ReminderBroadcaster.class);
        //put extras
        int itemType = StorageManager.getItemType(reminder);
        intent.setAction(String.valueOf(itemType));
        switch (Integer.parseInt(intent.getAction())) {
            case StorageManager.TEXT_TYPE:
                intent.putExtra("reminder", ParcelableUtil.marshall(((TextReminder) reminder)));
                break;
            case StorageManager.AUD_TYPE:
                intent.putExtra("reminder", ParcelableUtil.marshall(((AudioReminder) reminder)));
                break;
            case StorageManager.IMG_TYPE:
                intent.putExtra("reminder", ParcelableUtil.marshall(((PhotoReminder) reminder)));
                break;
        }
        PendingIntent intped = PendingIntent.getBroadcast(instance.context, reminder.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(reminder.getTime());
        calendar.set(Calendar.SECOND, 0);
        instance.alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intped);
    }

    public static Notification createNotification(Reminder reminder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            instance.nBuilder = new NotificationCompat.Builder(instance.context, CHANNEL_ID);
        } else {
            instance.nBuilder = new NotificationCompat.Builder(instance.context);
        }
        switch (StorageManager.getItemType(reminder)) {
            case TEXT_TYPE:
                instance.nBuilder.setSmallIcon(R.drawable.ic_baseline_alarm_24)
                        .setContentTitle(reminder.getTitle())
                        .setContentText(((TextReminder) reminder).getReminderText())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .setBigContentTitle(reminder.getTitle())
                                .bigText(((TextReminder) reminder).getReminderText())
                                .setSummaryText(instance.context.getString(R.string.summaryNofText)));
                break;
            case IMG_TYPE:
                // TODO: 19/1/2022 Crear Img notificación 
                break;
            case AUD_TYPE:
                // TODO: 19/1/2022 Crear AUD notificación
                break;
        }
        return instance.nBuilder.build();
    }
}

package romang.montejo.moya;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import romang.montejo.moya.Broadcaster.ReminderBroadcaster;
import romang.montejo.moya.Model.AudioReminder;
import romang.montejo.moya.Model.PhotoReminder;
import romang.montejo.moya.Model.Reminder;
import romang.montejo.moya.Model.TextReminder;
import romang.montejo.moya.Persistence.StorageManager;
import romang.montejo.moya.Util.ParcelableUtil;

public class NotificationsManager  {
    public static NotificationsManager instance;
    public static Context context;
    public AlarmManager alarmManager;

    private int importance = NotificationManager.IMPORTANCE_DEFAULT;
    static private String CHANNEL_NAME = "romang.montejo.moya";
    static public String CHANNEL_ID;

    public static final String PLAY_BUTTON = "romang.montejo.moya.PLAY";
    public static final String PAUSE_BUTTON = "romang.montejo.moya.PAUSE";


    public NotificationsManager(Context ctx) {
        context = ctx;
        CHANNEL_ID = String.valueOf(ctx.hashCode());
        startingChannel();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public NotificationsManager() {

    }
    static public void startingLauchNotifications(List<Reminder> reminderList) {
        if (PreferenceManager.getDefaultSharedPreferences(instance.context).getBoolean("notification", true)) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<Reminder> notifReminders = reminderList.stream().filter((a) -> a.getNoti()).collect(Collectors.toList());
                    for (Reminder reminder : notifReminders) {
                        lauchNotification(reminder);
                    }
                }
            });
        }
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
        if (PreferenceManager.getDefaultSharedPreferences(instance.context).getBoolean("notification", true)) {
            Intent intent = new Intent(instance.context, ReminderBroadcaster.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                instance.context.startForegroundService(intent);
            } else {
                instance.context.startService(intent);
            }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                instance.alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intped);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                instance.alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intped);
            } else {
                instance.alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intped);
            }
        }
    }

}

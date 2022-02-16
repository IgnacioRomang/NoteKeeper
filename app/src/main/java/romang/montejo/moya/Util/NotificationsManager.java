package romang.montejo.moya.Util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

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

public class NotificationsManager {
    public static NotificationsManager instance;
    public static Context context;
    static public String CHANNEL_ID = "2D2C9FY6ASK2A7PM4EL85E5W";
    static private String CHANNEL_NAME = "romang.montejo.moya";
    public AlarmManager alarmManager;
    private int importance = NotificationManager.IMPORTANCE_DEFAULT;


    public NotificationsManager() {
        startingChannel();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    static public NotificationsManager getInstance() {
        if (instance == null) {
            instance = new NotificationsManager();
        }
        return instance;
    }

    public void startingLauchNotifications(List<Reminder> reminderList) {
        if (PreferenceManager.getDefaultSharedPreferences(instance.context).getBoolean("notification", true)) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Calendar today = Calendar.getInstance();
                    List<Reminder> notifReminders = reminderList.stream().filter((x) -> x.getNoti() && x.getTime() >= today.getTimeInMillis()).collect(Collectors.toList());
                    for (Reminder reminder : notifReminders) {
                        lauchNotification(reminder);
                    }
                }
            });
        }
    }

    public void startingChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void lauchNotification(Reminder reminder) {
        if (PreferenceManager.getDefaultSharedPreferences(instance.context).getBoolean("notification", true) || reminder.getNoti() == true) {

            Intent intent = new Intent(instance.context, ReminderBroadcaster.class);

            int itemType = StorageManager.getItemType(reminder);
            intent.setAction(context.getPackageName() + ".Reminder");
            intent.putExtra("type", itemType);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            switch (itemType) {
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
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    instance.alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intped);
                } else {
                    instance.alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intped);
                }
            }
        }
    }

}

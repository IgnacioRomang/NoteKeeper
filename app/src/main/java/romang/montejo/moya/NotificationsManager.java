package romang.montejo.moya;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.media.MediaSessionManager;
import androidx.media2.SessionPlayer;
import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
    public static MediaPlayer mediaPlayer;


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
                PhotoReminder photoReminder = (PhotoReminder) reminder;
                Bitmap photo = BitmapFactory.decodeFile(photoReminder.getCurrentPhotoPath());
                instance.nBuilder.setSmallIcon(R.drawable.ic_baseline_alarm_24)
                        .setContentTitle(photoReminder.getTitle())
                        .setContentText("Image...")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(photo)
                                .bigLargeIcon(photo));
                break;
            case AUD_TYPE:
                AudioReminder audioReminder = (AudioReminder) reminder;
                mediaPlayer = MediaPlayer.create(instance.context, Uri.parse(audioReminder.filePath));

                MediaSession mSession = new MediaSession(instance.context, "debug tag");

                mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

                PlaybackState.Builder stateBuilder = new PlaybackState.Builder()
                        .setActions(PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE);

                mSession.setPlaybackState(stateBuilder.build());
                mSession.setActive(true);
                mSession.setCallback(new MediaSession.Callback() {
                    @Override
                    public void onPlay() {
                        mediaPlayer.start();
                        super.onPlay();
                    }

                    @Override
                    public void onPause() {
                        mediaPlayer.pause();
                        super.onPause();
                    }

                    @Override
                    public void onStop() {
                        mediaPlayer.stop();
                        super.onStop();
                    }

                    @Override
                    public void onSeekTo(long pos) {
                        mediaPlayer.seekTo(Long.signum(pos));
                        super.onSeekTo(pos);
                    }
                });
                MediaSession.Token token = mSession.getSessionToken();
                Bitmap bitmap = BitmapFactory.decodeResource(instance.context.getResources(),R.drawable.green_audio);
                instance.nBuilder.setSmallIcon(R.drawable.ic_baseline_alarm_24)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentTitle(audioReminder.getTitle())
                        .setLargeIcon(bitmap)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .addAction(new NotificationCompat.Action(R.drawable.ic_baseline_play_arrow_24,"play",null))
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(MediaSessionCompat.Token.fromToken(token))
                                .setShowActionsInCompactView(0));
                break;
        }
        return instance.nBuilder.build();
    }
}

package romang.montejo.moya.Broadcaster;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Parcelable;

import androidx.core.app.NotificationCompat;

import romang.montejo.moya.Model.AudioReminder;
import romang.montejo.moya.Model.PhotoReminder;
import romang.montejo.moya.Model.TextReminder;
import romang.montejo.moya.NotificationsManager;
import romang.montejo.moya.Persistence.StorageManager;
import romang.montejo.moya.MediaPlayerActivity;
import romang.montejo.moya.R;
import romang.montejo.moya.Util.ParcelableUtil;

public class ReminderBroadcaster extends BroadcastReceiver {
    public NotificationCompat.Builder nBuilder;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nBuilder = new NotificationCompat.Builder(context, NotificationsManager.CHANNEL_ID);
        } else {
            nBuilder = new NotificationCompat.Builder(context);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        switch (Integer.parseInt(intent.getAction())) {
            case StorageManager.TEXT_TYPE:
                TextReminder texreminder = ParcelableUtil.unmarshall(intent.getExtras().getByteArray("reminder"), TextReminder.CREATOR);
                nBuilder.setSmallIcon(R.drawable.ic_baseline_alarm_24)
                        .setContentTitle(texreminder.getTitle())
                        .setContentText(texreminder.getReminderText())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .setBigContentTitle(texreminder.getTitle())
                                .bigText(texreminder.getReminderText())
                                .setSummaryText(context.getString(R.string.summaryNofText)));
                break;
            case StorageManager.IMG_TYPE:
                PhotoReminder imgreminder = ParcelableUtil.unmarshall(intent.getExtras().getByteArray("reminder"), PhotoReminder.CREATOR);
                Bitmap photo = BitmapFactory.decodeFile(imgreminder.getCurrentPhotoPath());
                nBuilder.setSmallIcon(R.drawable.ic_baseline_alarm_24)
                        .setContentTitle(imgreminder.getTitle())
                        .setContentText("Image...")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(photo)
                                .bigLargeIcon(photo));
                break;
            case StorageManager.AUD_TYPE:
                AudioReminder audreminder = ParcelableUtil.unmarshall(intent.getExtras().getByteArray("reminder"), AudioReminder.CREATOR);
                intent = new Intent(context, MediaPlayerActivity.class);
                intent.putExtra("audio", ParcelableUtil.marshall(audreminder));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                nBuilder.setSmallIcon(R.drawable.ic_baseline_alarm_24)
                        .setContentTitle(audreminder.getTitle())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .addAction(new NotificationCompat.Action(R.drawable.ic_baseline_remove_red_eye_24, "see", pendingIntent))
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0))
                        .setAutoCancel(true);
                break;
        }
        notificationManager.notify(nBuilder.hashCode(), nBuilder.build());

    }
}

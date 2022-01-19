package romang.montejo.moya;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Random;

import romang.montejo.moya.Model.AudioReminder;
import romang.montejo.moya.Model.PhotoReminder;
import romang.montejo.moya.Model.TextReminder;
import romang.montejo.moya.Persistence.StorageManager;
import romang.montejo.moya.Util.ParcelableUtil;

public class ReminderBroadcaster extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        switch (Integer.parseInt(intent.getAction())) {
            case StorageManager.TEXT_TYPE:
                TextReminder texreminder = ParcelableUtil.unmarshall(intent.getExtras().getByteArray("reminder"), TextReminder.CREATOR);
                notification = NotificationsManager.createNotification(texreminder);
                break;
            case StorageManager.AUD_TYPE:
                AudioReminder audreminder = ParcelableUtil.unmarshall(intent.getExtras().getByteArray("reminder"), AudioReminder.CREATOR);
                notification = NotificationsManager.createNotification(audreminder);
                break;
            case StorageManager.IMG_TYPE:
                PhotoReminder imgreminder = ParcelableUtil.unmarshall(intent.getExtras().getByteArray("reminder"), PhotoReminder.CREATOR);
                notification = NotificationsManager.createNotification(imgreminder);
                break;
        }
        notificationManager.notify(notification.hashCode(), notification);
    }
}

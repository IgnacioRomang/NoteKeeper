package romang.montejo.moya;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import romang.montejo.moya.Model.AudioReminder;
import romang.montejo.moya.Model.PhotoReminder;
import romang.montejo.moya.Model.Reminder;
import romang.montejo.moya.Model.TextReminder;
import romang.montejo.moya.Holders.ReminderAdapter;
import romang.montejo.moya.Persistence.DbCallBacks;
import romang.montejo.moya.Persistence.StorageManager;
import romang.montejo.moya.Util.NotificationsManager;

public class MainViewModel extends ViewModel {
    public MutableLiveData<Calendar> calendarMutableLiveData;
    public MutableLiveData<List<Reminder>> liveData;
    public ReminderAdapter adapter;
    public String currentPath;
    private Context context;
    public Bitmap photo;

    public MutableLiveData<Boolean> getResult() {
        if (result == null) {
            result = new MutableLiveData<>();
            result.postValue(false);
        }
        return result;
    }

    public MutableLiveData<Boolean> result;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public List<Reminder> getList() {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
            liveData.setValue(new ArrayList<>());
        }
        return liveData.getValue();
    }

    public void setList(List<Reminder> list) {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }
        liveData.postValue(list);
    }

    public MutableLiveData<Calendar> getCalendarMutableLiveData() {
        if (calendarMutableLiveData == null) {
            calendarMutableLiveData = new MutableLiveData<Calendar>();
            calendarMutableLiveData.setValue(Calendar.getInstance());
        }
        return calendarMutableLiveData;
    }

    public void setCalendarMutableLiveData(MutableLiveData<Calendar> calendarMutableLiveData) {
        this.calendarMutableLiveData = calendarMutableLiveData;
    }

    public Calendar setDate(Calendar calen,Long selection) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(selection);
        Date c = calendar.getTime();
        int year, month, day;
        year = c.getYear() + 1900;
        month = c.getMonth();
        day = c.getDate() + 1;
        calen.set(Calendar.YEAR, year);
        calen.set(Calendar.MONTH, month);
        calen.set(Calendar.DAY_OF_MONTH, day);
        return calen;
    }

    public String getTimeString(Long time) {
        SimpleDateFormat formatoF = new SimpleDateFormat("dd/MM/YYYY - HH:mm");
        return formatoF.format(time);
    }

    public Calendar setTime(Calendar calendar,int hour, int min) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        return calendar;
    }

    public void createAudioReminder(String title, boolean checked, long record_time) {
        if (!checked) {
            calendarMutableLiveData.setValue(Calendar.getInstance());
        }
        AudioReminder reminder = new AudioReminder(title, calendarMutableLiveData.getValue().getTimeInMillis(), checked);
        reminder.setFilePath(currentPath);
        reminder.setRecordTime(record_time);
        saveReminder(reminder);
    }

    public void createTextReminder(String title, String reminderText, boolean checked) {
        if (!checked) {
            calendarMutableLiveData.setValue(Calendar.getInstance());
        }
        TextReminder reminder = new TextReminder(title, calendarMutableLiveData.getValue().getTimeInMillis(), checked, reminderText);
        saveReminder(reminder);
    }

    public void createPhotoReminder(String title, boolean checked) {
        if (!checked) {
            calendarMutableLiveData.setValue(Calendar.getInstance());
        }
        PhotoReminder reminder = new PhotoReminder(title, calendarMutableLiveData.getValue().getTimeInMillis(), checked);
        reminder.setCurrentPhotoPath(currentPath);
        saveReminder(reminder);
    }

    public void saveReminder(Reminder reminder) {
        List<Reminder> list;
        if (liveData == null) {
            liveData = new MutableLiveData<>();
            list = new ArrayList<>();
        } else {
            list = liveData.getValue();
        }
        if (reminder.getTime() >= (Calendar.getInstance().getTimeInMillis()-1000*120)) {
            list.add(reminder);
        }
        StorageManager.getInstance(null).addReminder(reminder, new DbCallBacks.saveResultCallback() {
            @Override
            public void result(boolean exito) {
                result.postValue(exito);
                Calendar calendar= Calendar.getInstance();
                int min = calendar.get(Calendar.MINUTE);
                calendar.set(Calendar.MINUTE,min-2);
                if (reminder.getNoti() && reminder.getTime()>= calendar.getTimeInMillis()) {
                    NotificationsManager.lauchNotification(reminder);
                }
            }
        });
        liveData.setValue(list);
    }

    public List<Reminder> filter(List<Reminder> list, List<Integer> chipChecketds) {
        List<Reminder> result = new ArrayList<>();
        for (int select : chipChecketds) {
            switch (select) {
                case R.id.textChip:
                    result.addAll(list.stream().filter((a) -> a instanceof TextReminder).collect(Collectors.toList()));
                    break;
                case R.id.photoChip:
                    result.addAll(list.stream().filter((a) -> a instanceof PhotoReminder).collect(Collectors.toList()));
                    break;
                case R.id.audioChip:
                    result.addAll(list.stream().filter((a) -> a instanceof AudioReminder).collect(Collectors.toList()));
                    break;
                case R.id.oldiesChip:
                    Date today = new Date();
                    today.setMinutes(today.getMinutes() - 5);// 5 min antes
                    result = result.stream().filter((x) -> x.getTime() >= today.getTime()).collect(Collectors.toList());
                    break;
            }
        }
        return result;
    }

}

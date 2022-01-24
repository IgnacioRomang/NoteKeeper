package romang.montejo.moya.Persistence;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import romang.montejo.moya.Holders.AudioReminderHolder;
import romang.montejo.moya.Holders.PhotoReminderHolder;
import romang.montejo.moya.Holders.TextReminderHolder;
import romang.montejo.moya.Model.AudioReminder;
import romang.montejo.moya.Model.PhotoReminder;
import romang.montejo.moya.Model.Reminder;
import romang.montejo.moya.Model.TextReminder;
import romang.montejo.moya.R;

public class StorageManager implements  DbCallBacks{
    private static final String DB_NAME ="romang.montejo.moya.Persistence";
    private DAO myDao;
    private static StorageManager instance;
    public static final int NOONE = 0;
    public static final int TEXT_TYPE = 1;
    public static final int IMG_TYPE = 2;
    public static final int AUD_TYPE = 3;

    public static int getItemType(Reminder reminder) {
        if (reminder instanceof TextReminder) {
            return TEXT_TYPE;
        } else {
            if (reminder instanceof PhotoReminder) {
                return IMG_TYPE;
            } else {
                if (reminder instanceof AudioReminder) {
                    return AUD_TYPE;
                }
            }
        }
        return 0;
    }

    private StorageManager(Context ctx){
        MyRoomDB db = Room.databaseBuilder(ctx,MyRoomDB.class,DB_NAME).fallbackToDestructiveMigration().build();
        myDao = db.Dao();
    }
    public static StorageManager getInstance(Context ctx){
        if(instance==null){
            instance = new StorageManager(ctx);
        }
        return instance;
    }

    public void addReminder(Reminder reminder,saveResultCallback callback){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                long rowId = 0;
                boolean result=false;
                switch (getItemType(reminder)){
                    case TEXT_TYPE:
                        rowId = myDao.insertTextReminder((TextReminder) reminder);
                        break;
                    case IMG_TYPE:
                        rowId = myDao.insertPhotoReminder((PhotoReminder) reminder);
                        break;
                    case AUD_TYPE:
                        rowId = myDao.insertAudioReminder((AudioReminder) reminder);
                        break;
                }
                if(rowId != OnConflictStrategy.IGNORE || rowId != OnConflictStrategy.FAIL|| rowId != OnConflictStrategy.ABORT){
                    result=true;
                }
                callback.result(result);
            }
        });
    }
    public void removeReminder(Reminder reminder){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                switch (getItemType(reminder)){
                    case TEXT_TYPE:
                        myDao.removeTextReminder((TextReminder) reminder);
                        break;
                    case IMG_TYPE:
                        myDao.removePhotoReminder((PhotoReminder) reminder);
                        removeFile(((PhotoReminder) reminder).getCurrentPhotoPath());
                        break;
                    case AUD_TYPE:
                        myDao.removeAudioReminder((AudioReminder) reminder);
                        removeFile(((AudioReminder) reminder).filePath);
                        break;
                }
            }
        });
    }
    private boolean removeFile(String path){
        File file = new File(path);
        boolean isDeleted =false;
        if(file.exists()){
            isDeleted = file.delete();
            if(isDeleted){
                Log.i("Dev: Nacho","Delete: "+path);
            }
        }
        return isDeleted;
    }
    public void getAllReminders(getRemainderCallback callback){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Reminder> result = new ArrayList<>();
                List<PhotoReminder>  l1 = myDao.getPhotoReminder();
                List<TextReminder>  l2 = myDao.getTextReminder();
                List<AudioReminder> l3 = myDao.getAudioReminder();
                result.addAll(l1);
                result.addAll(l2);
                result.addAll(l3);
                result.sort(new Comparator<Reminder>() {
                    @Override
                    public int compare(Reminder o1, Reminder o2) {
                        return Long.compare(o1.getTime(),o2.getTime());
                    }
                });
                boolean boolresult;
                if(result.isEmpty()){
                    boolresult = false;
                }
                else{
                    boolresult = true;
                }
                callback.result(boolresult,result);
            }
        });
    }

    public void getArchivedReminders(getRemainderCallback callback){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Reminder> result = new ArrayList<>();
                List<PhotoReminder>  l1 = myDao.getAllPhotoReminder();
                List<TextReminder>  l2 = myDao.getAllTextReminder();
                List<AudioReminder> l3 = myDao.getAllAudioReminder();
                result.addAll(l1);
                result.addAll(l2);
                result.addAll(l3);
                boolean boolresult;
                if(result.isEmpty()){
                    boolresult = false;
                }
                else{
                    boolresult = true;
                }
                Date today = new Date();
                today.setMinutes(today.getMinutes()-5);// 5 min antes
                callback.result(boolresult,result.stream().filter((x)-> x.getTime()<=today.getTime()).collect(Collectors.toList()));
            }
        });
    }


    public void getFindRemineders(String find,getRemainderCallback callback){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Reminder> result = new ArrayList<>();
                List<PhotoReminder>  l1 = myDao.findPhotoReminder(find);
                List<TextReminder>  l2 = myDao.findTextReminder(find);
                List<AudioReminder> l3 = myDao.findAudioReminder(find);
                result.addAll(l1);
                result.addAll(l2);
                result.addAll(l3);
                result.sort(new Comparator<Reminder>() {
                    @Override
                    public int compare(Reminder o1, Reminder o2) {
                        return Long.compare(o1.getTime(),o2.getTime());
                    }
                });
                boolean boolresult;
                if(result.isEmpty()){
                    boolresult = false;
                }
                else{
                    boolresult = true;
                }
                callback.result(boolresult,result);
            }
        });
    }
}

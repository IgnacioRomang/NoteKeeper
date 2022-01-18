package romang.montejo.moya.Model;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "PhotoReminder")
public class PhotoReminder extends Reminder {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String currentPhotoPath;

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
    }

    public PhotoReminder(String title, Long time, Boolean noti) {
        super(title, time, noti);
    }


    public String getNameFile() {
        return ("NoteKeeper_" + String.valueOf(this.getTime()));
    }
}

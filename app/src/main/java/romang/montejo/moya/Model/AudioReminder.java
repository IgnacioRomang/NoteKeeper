package romang.montejo.moya.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AudioReminder")
public class AudioReminder extends Reminder {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String filePath;
    public long recordTime;

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    public AudioReminder(String title, Long time, Boolean noti) {
        super(title, time, noti);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

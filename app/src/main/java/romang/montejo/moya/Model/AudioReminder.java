package romang.montejo.moya.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AudioReminder")
public class AudioReminder extends Reminder implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    public String filePath;
    public long recordTime;

    protected AudioReminder(Parcel in) {
        this(in.readString(),in.readLong(),in.readBoolean());
        filePath = in.readString();
        recordTime = in.readLong();
    }

    public static final Creator<AudioReminder> CREATOR = new Creator<AudioReminder>() {
        @Override
        public AudioReminder createFromParcel(Parcel in) {
            return new AudioReminder(in);
        }

        @Override
        public AudioReminder[] newArray(int size) {
            return new AudioReminder[size];
        }
    };

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getTitle());
        dest.writeLong(getTime());
        dest.writeBoolean(getNoti());

        dest.writeString(filePath);
        dest.writeLong(recordTime);
    }
}

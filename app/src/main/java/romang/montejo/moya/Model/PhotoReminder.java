package romang.montejo.moya.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "PhotoReminder")
public class PhotoReminder extends Reminder implements Parcelable {
    public static final Creator<PhotoReminder> CREATOR = new Creator<PhotoReminder>() {
        @Override
        public PhotoReminder createFromParcel(Parcel in) {
            return new PhotoReminder(in);
        }

        @Override
        public PhotoReminder[] newArray(int size) {
            return new PhotoReminder[size];
        }
    };
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String currentPhotoPath;

    protected PhotoReminder(Parcel in) {
        this(in.readString(), in.readLong(), in.readBoolean());
        currentPhotoPath = in.readString();
    }

    public PhotoReminder(String title, Long time, Boolean noti) {
        super(title, time, noti);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
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

        dest.writeString(currentPhotoPath);
    }
}

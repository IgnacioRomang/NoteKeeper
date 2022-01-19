package romang.montejo.moya.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import romang.montejo.moya.Util.ParcelableUtil;

@Entity(tableName = "PhotoReminder")
public class PhotoReminder extends Reminder implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String currentPhotoPath;

    protected PhotoReminder(Parcel in) {
        this(in.readString(),in.readLong(),in.readBoolean());
        currentPhotoPath = in.readString();
    }

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

    public PhotoReminder(String title, Long time, Boolean noti) {
        super(title, time, noti);
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

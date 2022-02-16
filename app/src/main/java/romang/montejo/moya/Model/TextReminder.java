package romang.montejo.moya.Model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TextReminder")
public class TextReminder extends Reminder implements Parcelable {
    public static final Creator<TextReminder> CREATOR = new Creator<TextReminder>() {
        @Override
        public TextReminder createFromParcel(Parcel in) {
            return new TextReminder(in);
        }

        @Override
        public TextReminder[] newArray(int size) {
            return new TextReminder[size];
        }
    };
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String reminderText;

    protected TextReminder(Parcel in) {
        this(in.readString(), in.readLong(), in.readBoolean(), in.readString());
    }

    public TextReminder(String title, Long time, Boolean noti, String reminderText) {
        super(title, time, noti);
        this.reminderText = reminderText;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReminderText() {
        return reminderText;
    }

    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
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

        dest.writeString(reminderText);
    }
}

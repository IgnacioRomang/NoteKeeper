package romang.montejo.moya.Model;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TextReminder")
public class TextReminder extends Reminder{
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String reminderText;

    public TextReminder(String title, Long time, Boolean noti, String reminderText) {
        super(title, time, noti);
        this.reminderText = reminderText;
    }

    public String getReminderText() {
        return reminderText;
    }

    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
    }

}

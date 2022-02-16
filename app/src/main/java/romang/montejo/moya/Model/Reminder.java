package romang.montejo.moya.Model;

public class Reminder {
    private String title;
    private Long time;
    private Boolean noti;

    public Reminder(String title, Long time, Boolean noti) {
        this.title = title;
        this.time = time;
        this.noti = noti;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Boolean getNoti() {
        return noti;
    }

    public void setNoti(Boolean noti) {
        this.noti = noti;
    }
}

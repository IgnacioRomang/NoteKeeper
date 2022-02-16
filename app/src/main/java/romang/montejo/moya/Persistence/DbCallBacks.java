package romang.montejo.moya.Persistence;

import java.util.List;

import romang.montejo.moya.Model.Reminder;

public interface DbCallBacks {

    public void getReminders(getRemainderCallback callback);

    public void addReminder(Reminder reminder, saveResultCallback callback);

    interface saveResultCallback {
        void result(final boolean exito);
    }

    interface getRemainderCallback {
        void result(final boolean exito, final List<Reminder> recordatorios);
    }

}

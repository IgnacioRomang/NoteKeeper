package romang.montejo.moya.Persistence;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import romang.montejo.moya.Model.AudioReminder;
import romang.montejo.moya.Model.PhotoReminder;
import romang.montejo.moya.Model.TextReminder;

@Database(entities = {TextReminder.class, PhotoReminder.class, AudioReminder.class}, version = 1)
public abstract class MyRoomDB extends RoomDatabase {
    public abstract DAO Dao();
}
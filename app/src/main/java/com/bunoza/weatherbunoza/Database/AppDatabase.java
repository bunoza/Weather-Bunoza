package com.bunoza.weatherbunoza.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Data.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DataDao dataDao();


}
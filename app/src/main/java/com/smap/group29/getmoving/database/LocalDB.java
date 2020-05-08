package com.smap.group29.getmoving.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.smap.group29.getmoving.model.NewUser;

import static com.smap.group29.getmoving.utils.GlobalConstants.DATABASE_NAME;

//@Database(entities = {NewUser.class}, version = 1, exportSchema = false)
//public abstract class LocalDB extends RoomDatabase {
//
//    private static LocalDB db;
//    public abstract LocalDao localDao();
//
//    public static LocalDB getDB(Context context) {
//        if (db == null){
//            db = Room.databaseBuilder(context.getApplicationContext(), LocalDB.class,DATABASE_NAME).fallbackToDestructiveMigration().build();
//        }
//        return db;
//    }
//}

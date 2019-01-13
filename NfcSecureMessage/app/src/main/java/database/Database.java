package database;

import android.content.Context;

public class Database {

    private static Database idatabase;
    private final DatabaseHelper mDbHelper;

    public Database(DatabaseHelper mDbHelper) {
        this.mDbHelper = mDbHelper;
    }

    public static Database getIstance(Context context){
        if(idatabase==null) {
            idatabase =new Database(new DatabaseHelper(context));
        }
        return idatabase;
    }
}

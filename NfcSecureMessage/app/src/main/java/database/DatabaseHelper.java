package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import utils.Logging;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = Logging.getTAG(DatabaseHelper.class);
    private static final String DATABASE_NAME = "NfcSecureMessage.db";

    private static final String SQL_CREATE_TABLE_MESSAGE =
        "CREATE TABLE " + Message.TABLE_NAME+ " (" +
        Message.COLUMN_NAME_ID              +" INTEGER PRIMARY KEY," +
        Message.COLUMN_NAME_AUTHOR          +" TEXT," +
        Message.COLUMN_NAME_RECIPIENT       +" TEXT," +
        Message.COLUMN_NAME_CONVERSATION    +" TEXT, " +
        Message.COLUMN_NAME_MESSSAGE        +" TEXT, " +
        Message.COLUMN_NAME_DATE            +" TEXT)";

    private static final String SQL_DELETE_TABLE_MESSAGE =
            "DROP TABLE IF EXISTS " + Message.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_MESSAGE);
        Log.d(TAG, "Database created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE_MESSAGE);
        Log.d(TAG, "Database dropped!");
    }
}

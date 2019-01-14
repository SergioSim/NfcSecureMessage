package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Database {

    private static Database idatabase;
    private final DatabaseHelper mDbHelper;

    public Database(DatabaseHelper mDbHelper) { this.mDbHelper = mDbHelper; }

    public static Database getIstance(Context context){
        if(idatabase==null) {
            idatabase =new Database(new DatabaseHelper(context));
        }
        return idatabase;
    }

    public List<Message> getMessageByConversation(String conversation) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                Message.COLUMN_NAME_ID,
                Message.COLUMN_NAME_AUTHOR,
                Message.COLUMN_NAME_RECIPIENT,
                Message.COLUMN_NAME_CONVERSATION,
                Message.COLUMN_NAME_MESSSAGE,
                Message.COLUMN_NAME_DATE
        };
        String selection = Message.COLUMN_NAME_CONVERSATION + " LIKE '" + conversation + "'";
        String[] selectionArgs = null;
        String sortOrder = "date(" + Message.COLUMN_NAME_DATE + ") DESC";
        Cursor cursor = db.query(
                Message.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,          // don't group the rows
                null,           // don't filter by row groups
                sortOrder               // The sort order
        );
        List<Message> messages = new ArrayList<>();
        while(cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(Message.COLUMN_NAME_ID));
            String author = cursor.getString(cursor.getColumnIndex(Message.COLUMN_NAME_AUTHOR));
            String recipient = cursor.getString(cursor.getColumnIndex(Message.COLUMN_NAME_RECIPIENT));
            String contact = cursor.getString(cursor.getColumnIndex(Message.COLUMN_NAME_CONVERSATION));
            String message = cursor.getString(cursor.getColumnIndex(Message.COLUMN_NAME_MESSSAGE));
            String date = cursor.getString(cursor.getColumnIndex(Message.COLUMN_NAME_DATE));
            messages.add(new Message((int) id, author, recipient, contact, message, date));
        }
        cursor.close();
        return messages;
    }

    public void addMessage(Message message) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Message.COLUMN_NAME_AUTHOR, message.getAuthor());
        values.put(Message.COLUMN_NAME_RECIPIENT, message.getRecipient());
        values.put(Message.COLUMN_NAME_CONVERSATION, message.getConversation());
        values.put(Message.COLUMN_NAME_MESSSAGE, message.getMessage());
        values.put(Message.COLUMN_NAME_DATE, message.getDate());
        db.insert(Message.TABLE_NAME, null, values);
    }

    public int deleteMessage(String conversation, Message message){
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            String selection =
                    Message.COLUMN_NAME_ID + " LIKE '" + message.getId() + "' AND " +
                    Message.COLUMN_NAME_AUTHOR + " LIKE '" + message.getAuthor() + "' AND " +
                    Message.COLUMN_NAME_RECIPIENT + " LIKE '" + message.getRecipient() + "' AND " +
                    Message.COLUMN_NAME_CONVERSATION + " LIKE '" + message.getConversation() + "' AND " +
                    Message.COLUMN_NAME_MESSSAGE + " LIKE '" + message.getConversation() + "' AND " +
                    Message.COLUMN_NAME_DATE + " LIKE '" + message.getDate() + "'";

            return db.delete(Message.TABLE_NAME, selection,null);
    }

}

package database;

import android.provider.BaseColumns;

public class Message implements BaseColumns {
    public static final String TABLE_NAME = "Message";
    public static final String COLUMN_NAME_ID = "Id";
    public static final String COLUMN_NAME_AUTHOR = "Author";
    public static final String COLUMN_NAME_RECIPIENT = "Recipient";
    public static final String COLUMN_NAME_CONVERSATION = "Conversation";
    public static final String COLUMN_NAME_MESSSAGE = "Message";
    public static final String COLUMN_NAME_DATE = "Date";
}

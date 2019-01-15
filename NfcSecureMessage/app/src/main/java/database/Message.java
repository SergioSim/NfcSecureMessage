package database;

import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements BaseColumns {
    public static final String TABLE_NAME = "Message";
    public static final String COLUMN_NAME_ID = "Id";
    public static final String COLUMN_NAME_AUTHOR = "Author";
    public static final String COLUMN_NAME_RECIPIENT = "Recipient";
    public static final String COLUMN_NAME_CONVERSATION = "Conversation";
    public static final String COLUMN_NAME_MESSSAGE = "Message";
    public static final String COLUMN_NAME_DATE = "Date";

    private int id;
    private String author;
    private String recipient;
    private String conversation;
    private String message;
    private String date;

    public Message(int id, String author, String recipient, String conversation, String message, String date) {
        this.id = id;
        this.author = author;
        this.recipient = recipient;
        this.conversation = conversation;
        this.message = message;
        this.date = date;
    }

    public Message(String author, String recipient, String conversation, String message, String date){
        this(-1, author, recipient, conversation, message, date);
    }

    public Message(String author, String recipient, String conversation, String message){
        this( author, recipient, conversation, message, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));
    }

    public Message(String author, String recipient, String message){
        this( author, recipient, recipient, message);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isMeTheAuthor(){
        if(recipient.equals(conversation)){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Message -> id: " + id + " author: " + author + " recipient: " + recipient +
                " conversation: " + conversation + " message: " + message + " date: " + date;
    }
}
package ans.mbds;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cryptoTools.CryptoTool;
import database.Database;
import database.Message;
import network.Address;
import network.Server;
import nfctools.NfcActivity;
import nfctools.NfcTag;
import utils.Logging;

public class InboxActivity extends NfcActivity implements MessageCellAdapterListener {

    public static final String TAG = Logging.getTAG(InboxActivity.class);

    private String login;
    private String contact;
    private int key;
    private Database db;
    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recycleView;
    private MessageCellAdapter mcAdapter = null;
    private boolean doDecrypt = false;
    Button readButton;
    Message currentMessage;
    private NfcTag nfcTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        SharedPreferences sharedPref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String loginAlias = getString(R.string.login_alias);
        db = Database.getIstance(this);
        login = sharedPref.getString(loginAlias, "");
        readButton = findViewById(R.id.readButton);
        readButton.setOnClickListener(view -> showReadFragment());
        initRecycleView();
    }

    private void initRecycleView() {
        recycleView = findViewById(R.id.recycler_view_inbox);
        updateRecyleView();
        recycleView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recycleView.setLayoutManager(llm);
    }

    private void updateRecyleView(){
        mcAdapter = new MessageCellAdapter(this, messageList , this);
        recycleView.setAdapter(mcAdapter);
        recycleView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!doDecrypt){
            new PerformGetTask().execute(login);
        }
        doDecrypt = false;
    }

    @Override
    public void textClicked(Message message, MessageCellAdapter.MyViewHolder holder) {
        if(contact == null) return;
        holder.itemView.setBackgroundColor(Color.YELLOW);
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Do you really want to mark message :\n\"" + message.getMessage() + "\"\n as readable with this key?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    message.setAuthor(contact);
                    message.setConversation(contact);
                    message.setMessage(nfcTag.encryptWithTag(message.getMessage(), false));
                    db.addMessage(message);
                    currentMessage = message;
                    new PerformDeleteTask().execute(message.getId());
                })
                .setNegativeButton(android.R.string.no, null).show();
        Timer timer = new Timer("move contact", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }, 1000);
    }

    @Override
    public boolean longtextClicked(Message message, MessageCellAdapter.MyViewHolder holder) {
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: "+intent.getAction());
        if(mNfc.onNewIntent(intent) && isDialogDisplayed){
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            mNfcReadFragment = (NFCReadFragment)getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
            String message = mNfcReadFragment.onNfcDetected(mNfc);
            nfcTag = new NfcTag(message, null);
            if(nfcTag.validate()){
                contact = nfcTag.getContact();
                Log.d(TAG, "got NfcTag: " + nfcTag.toString());
                    for(Message mess : messageList){
                        mess.setMessage(nfcTag.decryptWithTag(mess.getMessage(),false));
                    }
                    doDecrypt = true;
                    updateRecyleView();
            }else{
                Toast.makeText(this, getString(R.string.badKey), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class PerformGetTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            Server server = new Server();
            Log.i(TAG, "sending: " + strings[0]);
            return server.getRequest(Address.ETCHMSG, strings[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null || response.equals("")) {
                Toast.makeText(getApplicationContext(),
                        "Connection Error!", Toast.LENGTH_SHORT).show();
                return;
            }
            JsonElement jelement = new JsonParser().parse(response);
            JsonArray jarray = jelement.getAsJsonArray();
            if(jarray.size() == 0) {
                Toast.makeText(getApplicationContext(),
                        "No new Messages", Toast.LENGTH_SHORT).show();
                return;
            }

            messageList = new ArrayList<>();
            int id;
            String msg;
            String date;

            for(int i = 0; i < jarray.size(); i++){
                JsonObject jObject = jarray.get(i).getAsJsonObject();
                id = jObject.get("ID").getAsInt();
                msg = jObject.get("message").getAsString();
                date = jObject.get("date").getAsString();
                messageList.add(new Message(id, "",login, "", msg, date));
            }
            for(Message message : messageList) {
                Log.i(TAG, message.toString());
            }
            updateRecyleView();
        }
    }

    private class PerformDeleteTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... ints) {
            Server server = new Server();
            Log.i(TAG, "sending: " + ints[0]);
            return server.deleteRequest(Address.DELETEMSG, ints[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null || response.equals("")) {
                Toast.makeText(getApplicationContext(),
                        "Connection Error!", Toast.LENGTH_SHORT).show();
                return;
            }
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject jarray = jelement.getAsJsonObject();
            if(jarray.size() == 0) {
                Toast.makeText(getApplicationContext(),
                        "Move unsuccessful", Toast.LENGTH_SHORT).show();
                return;
            }
            if(jarray.get("succes").getAsString().equals("true")){
                Toast.makeText(InboxActivity.this, "message moved to conversation: " +  InboxActivity.this.currentMessage.getConversation(), Toast.LENGTH_SHORT).show();
            }
            onResume();
            updateRecyleView();
        }
    }
}

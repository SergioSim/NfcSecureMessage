package ans.mbds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import cryptoTools.CryptoTool;
import database.Message;
import network.Address;
import network.Server;
import nfctools.NfcActivity;
import utils.Logging;

public class InboxActivity extends NfcActivity implements MessageCellAdapterListener {

    public static final String TAG = Logging.getTAG(InboxActivity.class);

    private String login;
    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recycleView;
    private MessageCellAdapter mcAdapter = null;
    private boolean doDecrypt = false;
    Button readButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        SharedPreferences sharedPref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String loginAlias = getString(R.string.login_alias);
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
            String[] tagContent = message.split("\\|");
            Log.d(TAG, "tagContent: "+tagContent[0] + " tagLength: " + tagContent.length);
            if(tagContent.length == 2){
                Log.d(TAG, "tagLength: "+tagContent[1]);
                try {
                    int key = Integer.parseInt(tagContent[1]);
                    for(Message mess : messageList){
                        mess.setMessage(CryptoTool.decrypt(mess.getMessage(), key));
                    }
                    doDecrypt = true;
                    updateRecyleView();
                }catch (NumberFormatException nfe) {
                    Log.e(TAG, "NumberFormatException: " + nfe.getMessage());
                    Toast.makeText(this, getString(R.string.badKey), Toast.LENGTH_SHORT).show();
                }
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
            for(int i = 0; i < jarray.size(); i++){
                String str = jarray.get(i).getAsJsonObject().get("message").toString();
                str = str.substring(1, str.length() - 1);
                messageList.add(new Message("",login, "", str));
            }
            for(Message message : messageList) {
                Log.i(TAG, message.toString());
            }
            updateRecyleView();
        }
    }
}

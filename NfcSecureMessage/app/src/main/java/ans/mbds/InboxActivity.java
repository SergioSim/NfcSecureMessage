package ans.mbds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        SharedPreferences sharedPref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String loginAlias = getString(R.string.login_alias);
        login = sharedPref.getString(loginAlias, "");
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        new PerformGetTask().execute(login);
    }

    @Override
    public void textClicked(Message message, MessageCellAdapter.MyViewHolder holder) {

    }

    @Override
    public boolean longtextClicked(Message message, MessageCellAdapter.MyViewHolder holder) {
        return false;
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
            recycleView.getAdapter().notifyDataSetChanged();
        }
    }
}

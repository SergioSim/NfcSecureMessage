package ans.mbds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import network.Address;
import network.Server;
import utils.Logging;

public class InboxActivity extends AppCompatActivity {

    public static final String TAG = Logging.getTAG(InboxActivity.class);

    String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        SharedPreferences sharedPref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String loginAlias = getString(R.string.login_alias);
        login = sharedPref.getString(loginAlias, "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        new PerformGetTask().execute(login);
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
            ArrayList<String> messages = new ArrayList<>();
            for(int i = 0; i < jarray.size(); i++){
                messages.add(jarray.get(i).getAsJsonObject().get("message").toString());
            }
            for(String message : messages) {
                Log.i(TAG, message);
            }
        }
    }
}

package ans.mbds;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import database.Database;
import network.Address;
import network.Server;
import utils.Logging;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = Logging.getTAG(LoginActivity.class);

    private EditText login;
    private EditText password;
    private Button validBtn;
    private Button registerBtn;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = Database.getIstance(getApplicationContext());
        final Intent regIntent = new Intent(this, RegisterActivity.class);
        initViews(regIntent);
        JSONObject cred = new JSONObject();
        try {
            cred.put("login","a");
            cred.put("password", "a");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String message = cred.toString();
        new PerformPostTask().execute(message);
    }

    public void initViews(Intent regIntent) {
        login = findViewById(R.id.loginbox);
        password = findViewById(R.id.passbox);
        registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener((v) -> startActivity(regIntent));
        validBtn = findViewById(R.id.validBtn);
        validBtn.setOnClickListener((v) -> login());
    }

    private void login() {
        if (login.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    R.string.voidLogin, Toast.LENGTH_SHORT).show();
        } else if (password.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    R.string.voidPass, Toast.LENGTH_SHORT).show();
        } else {
            //TODO implement database check and service!
            //if (checkUser(login.getText().toString(), password.getText().toString()) != -1) {
            //for offline acces...
            //Intent intent = new Intent(getApplicationContext(), CheckMessagesService.class);
            //intent.putExtra("login",login.getText().toString());
            //intent.putExtra("password", password.getText().toString());
            //intent.putExtra("userID", db.readUserID(login.getText().toString()));
            //stopService(intent);
            //startService(intent);
            String loginTxt = login.getText().toString();
            String passTxt = password.getText().toString();
            String message = "{ \"login\":\"" + loginTxt + "\", \"password\":\"" + passTxt + "\" }";
            new PerformPostTask().execute(message);
        }
    }

    private class PerformPostTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            Server server = new Server();
            Log.i(LoginActivity.TAG, "sending: " + strings[0]);
            return server.postRequest(Address.LOGIN, strings[0], true);
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                Toast.makeText(getApplicationContext(),
                        "Connection Error!", Toast.LENGTH_SHORT).show();
            }
            Gson gson = new Gson();
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject jobject = jelement.getAsJsonObject();
            String succes = jobject.get("succes").getAsString();
            String msg = jobject.get("msg").getAsString();
            Log.i(LoginActivity.TAG, "result: succes: " + succes);
            Log.i(LoginActivity.TAG, "result: msg: " + msg);
            if(succes.equals("true")) {
                setButtonColor(Color.GREEN);
                final Intent main = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(main);
                Toast.makeText(getApplicationContext(),
                        R.string.successfulConnection, Toast.LENGTH_SHORT).show();
            }else{
                setButtonColor(Color.RED);
            }

        }
    }

    private void setButtonColor(int i){
        validBtn.setBackgroundColor(i);
        Timer timer = new Timer("ButtonTimer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LoginActivity.this.validBtn.setBackgroundColor(Color.LTGRAY);
            }
        }, 1000);
    }
}

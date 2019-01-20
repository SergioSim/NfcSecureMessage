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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import network.Address;
import network.Server;
import utils.PasswordCheck;

public class RegisterActivity extends AppCompatActivity {

    private EditText loginEdi;
    private EditText password1;
    private EditText password2;
    private Button regsterBtn;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
    }

    public void initViews() {
        loginEdi = findViewById(R.id.login);
        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
        regsterBtn = findViewById(R.id.inscriptionBtn);
        regsterBtn.setOnClickListener((v) -> register());
        backBtn = findViewById(R.id.retourBtn);
        backBtn.setOnClickListener((v) -> finish());
    }

    private void register() {
        if (loginEdi.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    R.string.voidLogin, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password1.length() == 0 || password2.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    R.string.voidPass, Toast.LENGTH_SHORT).show();
            return;
        }
        String login = loginEdi.getText().toString();
        String pass1 = password1.getText().toString();
        String pass2 = password2.getText().toString();
        if (PasswordCheck.isValid(pass1, pass2, this, regsterBtn)) {
            JSONObject cred = new JSONObject();
            try {
                cred.put("login", login);
                cred.put("password", pass1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String message = cred.toString();
            new PerformPostTask().execute(message);
        }
    }


    private class PerformPostTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            Server server = new Server();
            Log.i(LoginActivity.TAG, "sending: " + strings[0]);
            return server.postRequest(Address.CREATEUSER, strings[0], true);
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null || response.equals("")) {
                Toast.makeText(getApplicationContext(),
                        "Connection Error!", Toast.LENGTH_SHORT).show();
                return;
            }
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject jobject = jelement.getAsJsonObject();
            String succes = jobject.get("succes").getAsString();
            Log.i(LoginActivity.TAG, "result: succes: " + succes);
            if (succes.equals("true")) {
                PasswordCheck.setButtonColor(Color.GREEN, RegisterActivity.this.regsterBtn);
                final Intent loginAct = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginAct);
                Toast.makeText(getApplicationContext(),
                        R.string.successfulRegister, Toast.LENGTH_SHORT).show();
            } else {
                PasswordCheck.setButtonColor(Color.RED, RegisterActivity.this.regsterBtn);
                Toast.makeText(getApplicationContext(),
                        R.string.loginAlreadyExists, Toast.LENGTH_SHORT).show();
            }

        }
    }
}

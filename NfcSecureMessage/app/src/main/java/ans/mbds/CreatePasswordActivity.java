package ans.mbds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import crypto.AppPassword;
import utils.Logging;

public class CreatePasswordActivity extends AppCompatActivity {

    public static final String TAG = Logging.getTAG(CreatePasswordActivity.class);

    EditText password, password2;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
        password = findViewById(R.id.createPasswordEdi);
        password2 = findViewById(R.id.createPasswordEdi2);
        button = findViewById(R.id.createPasswordBtn);
        button.setOnClickListener( view -> handleClick());
    }

    private void handleClick() {
        String pass = password.getText().toString();
        String pass2 = password2.getText().toString();
        if( pass.equals("") || pass2.equals("")) {
            badPassMessage("No password entered");
        }else if(!pass.equals(pass2)){
            badPassMessage("First password don't matches second");
        }else if(passCheck(pass)){
            if(AppPassword.create(pass)){
                setButtonColor(Color.GREEN);
                byte[] encryptedPassword = AppPassword.getEncryptedText();
                SharedPreferences sharedPref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_encrypted_password), Base64.encodeToString(encryptedPassword, Base64.DEFAULT));
                editor.commit();
                Log.i(TAG, "encrypted password: " + Base64.encodeToString(encryptedPassword, Base64.DEFAULT));
                Toast.makeText(this, "Password Created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), EnterPasswordActivity.class);
                startActivity(intent);
                finish();
            }else{
                badPassMessage("Problem while saving password, please try another one...");
            }
        }
    }

    private boolean passCheck(String password){
        if(password.length() < 8){
            badPassMessage("Password is not eight characters long.");
            return false;
        }
        String upperCase = "(.*[A-Z].*)";
        if(!password.matches(upperCase)){
            badPassMessage("Password must contain at least one capital letter.");
            return false;
        }
        String numbers = "(.*[0-9].*)";
        if(!password.matches(numbers)){
            badPassMessage("Password must contain at least one number.");
            return false;
        }
        String specialChars = "(.*[ ! # @ $ % ^ & * ( ) - _ = + [ ] ; : ' \" , < . > / ?].*)";
        if(!password.matches(specialChars)){
            badPassMessage("Password must contain at least one special character.");
            return false;
        }
        String space = "(.*[   ].*)";
        if(password.matches(space)){
            badPassMessage("Password cannot contain a space.");
            return false;
        }
        if(password.startsWith("?")){
            badPassMessage("Password cannot start with '?'.");
            return false;
        }
        if(password.startsWith("!")){
            badPassMessage("Password cannot start with '!'.");
            return false;
        }
        return true;
    }

    private void badPassMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        setButtonColor(Color.RED);
    }

    private void setButtonColor(int i){
        button.setBackgroundColor(i);
        Timer timer = new Timer("ButtonTimer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CreatePasswordActivity.this.button.setBackgroundColor(Color.LTGRAY);
            }
        }, 1000);
    }
}

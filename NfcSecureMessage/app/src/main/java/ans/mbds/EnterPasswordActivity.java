package ans.mbds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import crypto.AppPassword;

public class EnterPasswordActivity extends AppCompatActivity {

    EditText ediPassword;
    Button button;
    String encryptedpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        ediPassword = findViewById(R.id.enterPasswordEdi);
        button = findViewById(R.id.enterPasswordBtn);
        SharedPreferences sharedPref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String passwordAlias = getString(R.string.saved_encrypted_password);
        encryptedpassword = sharedPref.getString(passwordAlias, "");

        button.setOnClickListener(view -> handleClick());
    }

    private void handleClick() {
        String rawPassword = ediPassword.getText().toString();
        if(AppPassword.isMatchingAppPassword(rawPassword, encryptedpassword)){
            setButtonColor(Color.GREEN);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            badPassMessage("Wrong Password");
        }
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
                EnterPasswordActivity.this.button.setBackgroundColor(Color.LTGRAY);
            }
        }, 1000);
    }
}

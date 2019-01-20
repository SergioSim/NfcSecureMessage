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

import cryptoTools.AppPassword;
import utils.Logging;
import utils.PasswordCheck;

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
        if(PasswordCheck.isValid(pass, pass2, this, button)){
            if(true) { // TODO replace true with PasswordCheck.passCheck(pass)
                if (AppPassword.create(pass)) {
                    PasswordCheck.setButtonColor(Color.GREEN, button);
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
                } else {
                    PasswordCheck.badPassMessage("Problem while saving password, please try another one...", this, button);
                }
            }
        }
    }
}

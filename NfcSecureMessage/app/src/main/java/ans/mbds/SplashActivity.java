package ans.mbds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import crypto.AppPassword;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sharedPref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String passwordAlias = getString(R.string.saved_encrypted_password);
        String encryptedpassword = sharedPref.getString(passwordAlias, "defaultValue...");
        Log.i("theencryptedpasswordis", encryptedpassword);
        Intent intent;
        if(AppPassword.isCreated()){
            intent = new Intent(getApplicationContext(), EnterPasswordActivity.class);
        }else{
            intent = new Intent(getApplicationContext(), CreatePasswordActivity.class);
        }
        startActivity(intent);
        finish();
    }
}

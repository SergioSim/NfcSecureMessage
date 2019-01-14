package ans.mbds;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import nfctools.Nfc;
import utils.Logging;

public class GenerateKeyActivity extends AppCompatActivity implements Listener {

    public static final String TAG = Logging.getTAG(GenerateKeyActivity.class);

    EditText cesarCipherEdi;
    CheckBox cesarCheck;
    EditText name;
    EditText password;
    Button btn;
    private NFCWriteFragment mNfcWriteFragment;
    int cesarkey = 0;
    boolean isDialogDisplayed = false;
    private Nfc mNfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_key);
        initViews();
        initNFC();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNfc.startListening(this, getClass());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfc.stopListening(this);
    }

    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed = false;
    }

    private void initViews() {
        cesarCipherEdi = findViewById(R.id.cesarcipherkey);
        cesarCheck = findViewById(R.id.checkCesar);
        name = findViewById(R.id.genContactName);
        password = findViewById(R.id.genPassword);
        btn = findViewById(R.id.genButton);
        btn.setOnClickListener(v -> onClick());
    }

    private void onClick() {
        if(checkInput()){
            showWriteFragment();
        }
    }

    private void initNFC(){
        mNfc = new Nfc(this);
    }

    private boolean checkInput() {
        if(!cesarCheck.isChecked()){
            setButtonColor(Color.RED);
            Toast.makeText(this, "You have to check at least one encryption mode!", Toast.LENGTH_SHORT).show();
            return false;
        }
        try{
            String value = cesarCipherEdi.getText().toString();
            int finalValue = Integer.parseInt(value);
            if(finalValue == 0){
                setButtonColor(Color.RED);
                Toast.makeText(this, "Cesar Cipher can't be 0!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }catch (NumberFormatException nfe){
            setButtonColor(Color.RED);
            Toast.makeText(this, "Cesar Cipher can't be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(name.getText().toString().equals("")){
            setButtonColor(Color.RED);
            Toast.makeText(this, "Name can't be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.getText().toString().equals("")){
            setButtonColor(Color.RED);
            Toast.makeText(this, "Password can't be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showWriteFragment() {
        mNfcWriteFragment = (NFCWriteFragment) getSupportFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
        if (mNfcWriteFragment == null) {
            mNfcWriteFragment = NFCWriteFragment.newInstance();
        }
        mNfcWriteFragment.show(getSupportFragmentManager(),NFCWriteFragment.TAG);
    }

    private void setButtonColor(int i){
        btn.setBackgroundColor(i);
        Timer timer = new Timer("ButtonTimer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                GenerateKeyActivity.this.btn.setBackgroundColor(Color.LTGRAY);
            }
        }, 1000);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: "+intent.getAction());
        if(mNfc.onNewIntent(intent)){
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            if (isDialogDisplayed) {
                String messageToWrite = name.getText().toString() + "|" + cesarkey;
                Log.d(TAG, "writing message : "+ messageToWrite);
                mNfcWriteFragment = (NFCWriteFragment) getSupportFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
                mNfcWriteFragment.onNfcDetected(mNfc, messageToWrite);
            }
        }
    }
}

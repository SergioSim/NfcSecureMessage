package ans.mbds;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import nfctools.Nfc;
import utils.Logging;

public class MainActivity extends AppCompatActivity implements Listener{

    public static final String TAG = Logging.getTAG(MainActivity.class);

    private EditText mEtMessage;
    private Button mBtWrite;
    private Button mBtRead;
    private Button mBtGen;
    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;
    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;
    private Nfc mNfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initNFC();
    }

    private void initViews() {
        mEtMessage = findViewById(R.id.et_message);
        mBtWrite = findViewById(R.id.btn_write);
        mBtRead = findViewById(R.id.btn_read);
        mBtGen = findViewById(R.id.btn_generateNfcTag);
        mBtWrite.setOnClickListener(view -> showWriteFragment());
        mBtRead.setOnClickListener(view -> showReadFragment());
        mBtGen.setOnClickListener(view -> goToGenPage());
    }

    private void initNFC(){
        mNfc = new Nfc(this);
    }

    private void showWriteFragment() {
        isWrite = true;
        mNfcWriteFragment = (NFCWriteFragment) getSupportFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
        if (mNfcWriteFragment == null) {
            mNfcWriteFragment = NFCWriteFragment.newInstance();
        }
        mNfcWriteFragment.show(getSupportFragmentManager(),NFCWriteFragment.TAG);
    }

    private void showReadFragment() {
        mNfcReadFragment = (NFCReadFragment) getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
        if (mNfcReadFragment == null) {
            mNfcReadFragment = NFCReadFragment.newInstance();
        }
        mNfcReadFragment.show(getSupportFragmentManager(),NFCReadFragment.TAG);
    }

    private void goToGenPage() {
        Intent intent = new Intent(this, GenerateKeyActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed = false;
        isWrite = false;
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
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: "+intent.getAction());
        if(mNfc.onNewIntent(intent)){
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            if (isDialogDisplayed) {
                if (isWrite) {
                    String messageToWrite = mEtMessage.getText().toString();
                    mNfcWriteFragment = (NFCWriteFragment) getSupportFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
                    mNfcWriteFragment.onNfcDetected(mNfc,messageToWrite);
                } else {
                    mNfcReadFragment = (NFCReadFragment)getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                    mNfcReadFragment.onNfcDetected(mNfc);
                }
            }
        }
    }
}
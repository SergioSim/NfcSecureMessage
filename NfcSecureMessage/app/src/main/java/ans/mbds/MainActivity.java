package ans.mbds;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import nfctools.NfcActivity;
import utils.Logging;

public class MainActivity extends NfcActivity {

    public static final String TAG = Logging.getTAG(MainActivity.class);

    private EditText mEtMessage;
    private Button mBtWrite;
    private Button mBtRead;
    private Button mBtGen;
    private Button mBtInbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mEtMessage = findViewById(R.id.et_message);
        mBtWrite = findViewById(R.id.btn_write);
        mBtRead = findViewById(R.id.btn_read);
        mBtGen = findViewById(R.id.btn_generateNfcTag);
        mBtInbox = findViewById(R.id.btn_inbox);
        mBtWrite.setOnClickListener(view -> showWriteFragment());
        mBtRead.setOnClickListener(view -> showReadFragment());
        mBtGen.setOnClickListener(view -> goToGenPage());
        mBtInbox.setOnClickListener(view -> goToInbox());
    }

    private void goToInbox() {
        Intent intent = new Intent(this, InboxActivity.class);
        startActivity(intent);
    }

    private void goToGenPage() {
        Intent intent = new Intent(this, GenerateKeyActivity.class);
        startActivity(intent);
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
                    if(mNfcWriteFragment != null)
                    mNfcWriteFragment.onNfcDetected(mNfc,messageToWrite);
                } else {
                    mNfcReadFragment = (NFCReadFragment)getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                    String message = mNfcReadFragment.onNfcDetected(mNfc);
                    String[] tagContent = message.split("\\|");
                    Log.d(TAG, "tagContent: "+tagContent[0]);
                    Log.d(TAG, "tagLength: "+tagContent.length);
                    if(tagContent.length == 2){
                        Intent conversationIntent = new Intent(this, ConversationActivity.class);
                        conversationIntent.putExtra("contact", tagContent[0]);
                        startActivity(conversationIntent);
                    }
                }
            }
        }
    }
}
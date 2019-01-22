package ans.mbds;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import nfctools.NfcActivity;
import nfctools.NfcTag;
import utils.Logging;

public class MainActivity extends NfcActivity {

    public static final String TAG = Logging.getTAG(MainActivity.class);

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
        mBtRead = findViewById(R.id.btn_read);
        mBtGen = findViewById(R.id.btn_generateNfcTag);
        mBtInbox = findViewById(R.id.btn_inbox);
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
                if (!isWrite) {
                    mNfcReadFragment = (NFCReadFragment) getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                    String message = mNfcReadFragment.onNfcDetected(mNfc);
                    NfcTag nfcTag = new NfcTag(message, null);
                    if (nfcTag.validate()) {
                        Intent conversationIntent = new Intent(this, ConversationActivity.class);
                        conversationIntent.putExtra("contact", nfcTag.getContact());
                        startActivity(conversationIntent);
                    }else{
                        Toast.makeText(this, "Bad Key!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
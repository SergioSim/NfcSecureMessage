package ans.mbds;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import nfctools.NfcActivity;
import nfctools.NfcTag;
import utils.Logging;

public class AddKeyActivity extends NfcActivity {

    public static final String TAG = Logging.getTAG(AddKeyActivity.class);

    private String password;
    private String oldTagContent;
    private int cesarkey;
    private boolean isTagged = false;
    private Button readBtn;
    private Button finishBtn;

    private NfcTag nfcTag;
    private NfcTag friendNfcTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_key);
        nfcTag = (NfcTag) getIntent().getParcelableExtra("nfcTag");
        Log.i(TAG, "creating AddKeyActivity" + nfcTag.toString());
        if(nfcTag == null){
            Toast.makeText(this, "Sorry somethink went wrong...", Toast.LENGTH_SHORT );
            finish();
            return;
        }
        initViews();
    }

    private void initViews(){
        readBtn = findViewById(R.id.readFriendKeyBtn);
        readBtn.setOnClickListener(view -> readButtonClicked());
        readBtn.setBackgroundColor(Color.BLUE);
        finishBtn = findViewById(R.id.finishKeyBtn);
        finishBtn.setOnClickListener(view -> finshButtonClicked());
    }

    private void finshButtonClicked() {
        if(isTagged){
            showWriteFragment();
        }else{
            Toast.makeText(this, "You have first to Read the Friends tag!", Toast.LENGTH_SHORT).show();
        }
    }

    private void readButtonClicked() {
        if(!isTagged){
            showReadFragment();
        }else{
            Toast.makeText(this, "Tag already read!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: "+intent.getAction());
        if(mNfc.onNewIntent(intent)){
            if (isDialogDisplayed) {
                if(isWrite) {
                    mNfcWriteFragment = (NFCWriteFragment) getSupportFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
                    if(mNfcWriteFragment.onNfcDetected(mNfc, nfcTag.getDecryptedTag())){
                        Toast.makeText(this, "Written: " + nfcTag.getDecryptedTag(), Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(this, R.string.badKey, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mNfcReadFragment = (NFCReadFragment)getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                    oldTagContent = mNfcReadFragment.onNfcDetected(mNfc);
                    friendNfcTag = new NfcTag(oldTagContent, null);
                    if(friendNfcTag.halfValidate()){
                        Log.i(TAG, "Friend TAG Content: " + friendNfcTag.toString());
                        nfcTag.append(friendNfcTag);
                        Log.i(TAG, "My TAG Content: " + nfcTag.toString());
                        Toast.makeText(this, "Read: " + oldTagContent, Toast.LENGTH_SHORT).show();
                        isTagged = true;
                        readBtn.setBackgroundColor(Color.GREEN);
                        finishBtn.setBackgroundColor(Color.BLUE);
                    }else{
                        Toast.makeText(this, R.string.badKey, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}

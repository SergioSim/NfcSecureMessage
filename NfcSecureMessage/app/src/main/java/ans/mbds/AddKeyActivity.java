package ans.mbds;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import nfctools.NfcActivity;
import utils.Logging;

public class AddKeyActivity extends NfcActivity {

    public static final String TAG = Logging.getTAG(AddKeyActivity.class);

    private String password;
    private String oldTagContent;
    private int cesarkey;
    private boolean isTagged = false;
    private Button readBtn;
    private Button finishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_key);
        password = getIntent().getStringExtra("password");
        cesarkey = getIntent().getIntExtra("cesarkey", 0);
        if(password == null || cesarkey == 0){
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
                    if(mNfcWriteFragment.onNfcDetected(mNfc, oldTagContent + "|" + cesarkey)){
                        Toast.makeText(this, "Written: " + oldTagContent + "|" + cesarkey, Toast.LENGTH_SHORT).show();
                        finish();
                    }else{

                    }
                }else{
                    mNfcReadFragment = (NFCReadFragment)getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                    oldTagContent = mNfcReadFragment.onNfcDetected(mNfc);
                    if(oldTagContent != null && !oldTagContent.equals("") && oldTagContent.split("\\|").length == 2){
                        //its a valid tag
                        Toast.makeText(this, "Read: " + oldTagContent, Toast.LENGTH_SHORT).show();
                        isTagged = true;
                        readBtn.setBackgroundColor(Color.GREEN);
                        finishBtn.setBackgroundColor(Color.BLUE);
                    }else {
                        Toast.makeText(this, R.string.badKey, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}

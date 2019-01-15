package nfctools;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ans.mbds.Listener;
import ans.mbds.NFCReadFragment;
import ans.mbds.NFCWriteFragment;

public class NfcActivity extends AppCompatActivity implements Listener {

    private Nfc mNfc;
    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;
    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNFC();
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

    private void initNFC(){
        mNfc = new Nfc(this);
    }
}

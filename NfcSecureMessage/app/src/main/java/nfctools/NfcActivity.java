package nfctools;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ans.mbds.Listener;
import ans.mbds.NFCReadFragment;
import ans.mbds.NFCWriteFragment;

public class NfcActivity extends AppCompatActivity implements Listener {

    protected Nfc mNfc;
    protected NFCWriteFragment mNfcWriteFragment;
    protected NFCReadFragment mNfcReadFragment;
    protected boolean isDialogDisplayed = false;
    protected boolean isWrite = false;

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
    protected void onResume() {
        super.onResume();
        mNfc.startListening(this, getClass());
    }

    @Override
    public void onDialogDisplayed() { isDialogDisplayed = true; }

    @Override
    public void onDialogDismissed() { isDialogDisplayed = false; }

    protected void initNFC(){ mNfc = new Nfc(this); }

    protected void showWriteFragment() {
        isWrite = true;
        mNfcWriteFragment = (NFCWriteFragment) getSupportFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
        if (mNfcWriteFragment == null) {
            mNfcWriteFragment = NFCWriteFragment.newInstance();
        }
        mNfcWriteFragment.show(getSupportFragmentManager(),NFCWriteFragment.TAG);
    }

    protected void showReadFragment() {
        mNfcReadFragment = (NFCReadFragment) getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
        if (mNfcReadFragment == null) {
            mNfcReadFragment = NFCReadFragment.newInstance();
        }
        mNfcReadFragment.show(getSupportFragmentManager(),NFCReadFragment.TAG);
    }
}

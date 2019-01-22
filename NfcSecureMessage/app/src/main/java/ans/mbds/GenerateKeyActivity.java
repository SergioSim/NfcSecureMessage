package ans.mbds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.CheckedInputStream;

import cryptoTools.AESEnCryptor;
import nfctools.NfcActivity;
import nfctools.NfcTag;
import utils.Logging;

public class GenerateKeyActivity extends NfcActivity {

    public static final String TAG = Logging.getTAG(GenerateKeyActivity.class);

    EditText cesarCipherEdi;
    EditText vigenereCipherEdi;
    CheckBox cesarCheck;
    CheckBox vigenereCheck;
    CheckBox aesCheck;
    EditText password;
    Button btn;
    int cesarkey = 0;
    int vigenerekey = 0;

    NfcTag nfcTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_key);
        initViews();
    }

    private void initViews() {
        cesarCipherEdi = findViewById(R.id.cesarcipherkey);
        vigenereCipherEdi = findViewById(R.id.vinegerecipherkey);
        cesarCheck = findViewById(R.id.checkCesar);
        vigenereCheck = findViewById(R.id.checkVigenere);
        aesCheck = findViewById(R.id.checkAES);
        password = findViewById(R.id.genPassword);
        btn = findViewById(R.id.genButton);
        btn.setOnClickListener(v -> onClick());
    }

    private void onClick() {
        if(checkInput()){
            nfcTag = new NfcTag();
            nfcTag.setPassword(password.getText().toString());
            setContact();
            setHeader();
            setKeys();
            nfcTag.halfPopulateDecryptedTag();
            Log.i(TAG, "status of nftTag: " + (nfcTag.halfValidate() ? "Valid":"Invalid"));
            if(nfcTag.halfValidate()){
                showWriteFragment();
            } else {
                Toast.makeText(this, "Somethink went wrong...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setKeys() {
        if(cesarCheck.isChecked()) nfcTag.setCesarKey(cesarkey);
        if(vigenereCheck.isChecked()) nfcTag.setVigenereKey(vigenerekey);
        if(aesCheck.isChecked()) nfcTag.setAesKey(generateAesKey());
    }

    private byte[] generateAesKey() {
        try {
            return AESEnCryptor.verySimpleGenerateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setHeader() {
        int cesarHeader = cesarCheck.isChecked() ? 1:0;
        int vinegereHeader = vigenereCheck.isChecked() ? 1:0;
        int aesHeader = aesCheck.isChecked() ? 1:0;
        int[] aHeader = {cesarHeader, vinegereHeader, aesHeader};
        nfcTag.setHeader(aHeader);
    }

    private void setContact() {
        SharedPreferences sharedPref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String loginAlias = getString(R.string.login_alias);
        String contactName = sharedPref.getString(loginAlias, "");
        nfcTag.setContact(contactName);
    }

    private boolean checkInput() {
        if(!cesarCheck.isChecked() && !vigenereCheck.isChecked() && !aesCheck.isChecked()){
            setButtonColor(Color.RED);
            Toast.makeText(this, "You have to check at least one encryption mode!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(cesarCheck.isChecked() || vigenereCheck.isChecked()) {
            try{
                String cesarValue = cesarCipherEdi.getText().toString();
                String vigenereValue = vigenereCipherEdi.getText().toString();
                cesarkey = Integer.parseInt(cesarValue);
                vigenerekey = Integer.parseInt(vigenereValue);
                if(cesarkey == 0){
                    setButtonColor(Color.RED);
                    Toast.makeText(this, "Cesar or Vigenere Cipher can't be 0!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }catch (NumberFormatException nfe){
                setButtonColor(Color.RED);
                Toast.makeText(this, "Cesar or Vigenere Cipher can't be empty!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(password.getText().toString().equals("")){
            setButtonColor(Color.RED);
            Toast.makeText(this, "Password can't be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
                String messageToWrite = nfcTag.getDecryptedTag();
                Log.d(TAG, "writing message : "+ messageToWrite);
                mNfcWriteFragment = (NFCWriteFragment) getSupportFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
                if(mNfcWriteFragment.onNfcDetected(mNfc, messageToWrite)){
                    Intent addKeyIntent = new Intent(this, AddKeyActivity.class);
                    addKeyIntent.putExtra("nfcTag", nfcTag);
                    startActivity(addKeyIntent);
                    finish();
                }
            }
        }
    }
}

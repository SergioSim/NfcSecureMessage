package nfctools;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * How To:
 * 1 - create new Nfc(context)
 * 2 - startListening(activity, class)
 * 3 - onNewIntent(intent)
 * 4 - then you can read(string) / write(string) or rawRead(bytes) / rawWrite(bytes)
 * 5 - don't forget to stopListening(activity)
 * **/
public class Nfc {

    private NfcAdapter mNfcAdapter;
    private Context context;
    private Ndef ndef;

    public Nfc(Context context){
        this.context = context;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
    }

    public void startListening(Activity activity, Class iclass){
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, iclass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(mNfcAdapter!= null)
            mNfcAdapter.enableForegroundDispatch(activity, pendingIntent, nfcIntentFilter, null);
    }

    public void stopListening(Activity activity){
        if(mNfcAdapter!= null)
            mNfcAdapter.disableForegroundDispatch(activity);
    }

    public boolean onNewIntent(Intent intent){
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(tag != null) {
            ndef = Ndef.get(tag);
            return true;
        }
        return false;
    }


    public String read(){
        String message = "null";
        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            if(ndefMessage != null) {
                message = new String(ndefMessage.getRecords()[0].getPayload());
            }
            ndef.close();
        } catch (IOException | FormatException e) {
            e.printStackTrace();
        }
        return message;
    }

    public byte[] rawRead(){
        byte[] rawMessage = null;
        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            if(ndefMessage != null) {
                rawMessage = ndefMessage.getRecords()[0].getPayload();
            }
            ndef.close();
        } catch (IOException | FormatException e) {
            e.printStackTrace();
        }
        return rawMessage;
    }

    public boolean write(String str){
        if (ndef != null) {
            try {
                ndef.connect();
                NdefRecord mimeRecord = NdefRecord.createMime("text/plain", str.getBytes(Charset.forName("US-ASCII")));
                ndef.writeNdefMessage(new NdefMessage(mimeRecord));
                ndef.close();
            } catch (IOException | FormatException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean rawWrite(byte[] messageBytes){
        if (ndef != null) {
            try {
                ndef.connect();
                NdefRecord mimeRecord = NdefRecord.createMime("application/octet-stream", messageBytes);
                ndef.writeNdefMessage(new NdefMessage(mimeRecord));
                ndef.close();
            } catch (IOException | FormatException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}

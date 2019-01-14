package ans.mbds;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cryptoTools.CryptoTool;
import database.Database;
import database.Message;
import nfctools.Nfc;
import utils.Logging;

public class ConversationActivity extends AppCompatActivity implements MessageCellAdapterListener, Listener{

    public static final String TAG = Logging.getTAG(ConversationActivity.class);

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private String contact;
    private Database db;
    private MessageCellAdapter mcAdapter = null;
    private Button btn;
    private EditText text;
    private NFCReadFragment mNfcReadFragment;
    private boolean isDialogDisplayed = false;
    private Nfc mNfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        contact = getIntent().getStringExtra("contact");
        if(contact == null){
            Toast.makeText(this, "Sorry somethink went wrong...", Toast.LENGTH_SHORT );
            finish();
            return;
        }
        db = Database.getIstance(this);
        initRecycleView();
        initViews();
        initNFC();
    }

    private void initViews(){
        btn = findViewById(R.id.messageSendButton);
        btn.setOnClickListener(v -> onClick());
        text = findViewById(R.id.messageText);
    }

    private void initRecycleView(){
        updateMessageList();
        mcAdapter = new MessageCellAdapter(this, messageList , this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(mcAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
    }

    private void onClick() {
        showReadFragment();
        onResume();
    }

    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMessageList();
        Log.i(TAG, "onResume Called! size of messageList: " + messageList.size());
        mcAdapter = new MessageCellAdapter(this, messageList , this);
        recyclerView.setAdapter(mcAdapter);
        recyclerView.getAdapter().notifyDataSetChanged();
        mNfc.startListening(this, getClass());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfc.stopListening(this);
    }

    private void initNFC(){
        mNfc = new Nfc(this);
    }

    private void updateMessageList() {
        messageList = db.getMessageByConversation(contact);
    }

    private void saveMessage(String encryptedText){
        Message message = new Message("Me", contact, contact, encryptedText);
        db.addMessage(message);
        text.setText(null);
    }

    private void showReadFragment() {
        mNfcReadFragment = (NFCReadFragment) getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
        if (mNfcReadFragment == null) {
            mNfcReadFragment = NFCReadFragment.newInstance();
        }
        mNfcReadFragment.show(getSupportFragmentManager(),NFCReadFragment.TAG);
    }

    @Override
    public void textClicked(Message message, MessageCellAdapter.MyViewHolder holder) {
        Log.i(TAG, "message clicked: " + message.getMessage() + " id:" + message.getId());
    }

    @Override
    public boolean longtextClicked(Message message, MessageCellAdapter.MyViewHolder holder) {
        Log.i(TAG, "message long clicked: " + message.getMessage());
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: "+intent.getAction());
        if(mNfc.onNewIntent(intent)){
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            if (isDialogDisplayed) {
                    mNfcReadFragment = (NFCReadFragment)getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                    String message = mNfcReadFragment.onNfcDetected(mNfc);
                    String[] tagContent = message.split("\\|");
                    if(tagContent.length == 2){
                        String theText = text.getText().toString();
                        try {
                            theText = CryptoTool.encrypt(theText, Integer.parseInt(tagContent[1]));
                            saveMessage(theText);
                        }catch (NumberFormatException nfe){
                            Toast.makeText(this, "Bad Key!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
    }
}

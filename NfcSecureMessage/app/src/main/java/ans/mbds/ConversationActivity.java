package ans.mbds;

import android.content.Intent;
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
import nfctools.NfcActivity;
import utils.Logging;

public class ConversationActivity extends NfcActivity implements MessageCellAdapterListener{

    public static final String TAG = Logging.getTAG(ConversationActivity.class);

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recycleView;
    private String contact;
    private Database db;
    private MessageCellAdapter mcAdapter = null;
    private Button btn;
    private EditText text;
    private boolean doDecrypt = false;
    int idToDecrypt;

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
    }

    private void initViews(){
        btn = findViewById(R.id.messageSendButton);
        btn.setOnClickListener(v -> onClick());
        text = findViewById(R.id.messageText);
    }

    private void initRecycleView(){
        recycleView = findViewById(R.id.recycler_view);
        updateRecyleView();
        recycleView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recycleView.setLayoutManager(llm);
    }

    private void onClick() {
        showReadFragment();
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!doDecrypt){
            updateRecyleView();
        }
        doDecrypt = false;
        recycleView.getAdapter().notifyDataSetChanged();
    }

    private void updateRecyleView(){
        messageList = db.getMessageByConversation(contact);
        Log.i(TAG, "onResume Called! size of messageList: " + messageList.size());
        mcAdapter = new MessageCellAdapter(this, messageList , this);
        recycleView.setAdapter(mcAdapter);
    }

    private void saveMessage(String encryptedText){
        Message message = new Message("Me", contact, contact, encryptedText);
        db.addMessage(message);
        text.setText(null);
    }

    @Override
    public void textClicked(Message message, MessageCellAdapter.MyViewHolder holder) {
        Log.i(TAG, "message clicked: " + message.getMessage() + " id:" + message.getId());
        doDecrypt = true;
        idToDecrypt = holder.getAdapterPosition();
        showReadFragment();
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
                    if(doDecrypt){
                        try {
                            Log.d(TAG, "id : "+ idToDecrypt);
                            String theText = messageList.get(idToDecrypt).getMessage();
                            theText = CryptoTool.decrypt(theText, Integer.parseInt(tagContent[1]));
                            Message message1 = messageList.get(idToDecrypt);
                            message1.setMessage(theText);
                            messageList.set(idToDecrypt, message1);
                            Log.d(TAG, messageList.get(idToDecrypt).toString());
                            mcAdapter = new MessageCellAdapter(ConversationActivity.this, messageList , ConversationActivity.this);
                            recycleView.setAdapter(mcAdapter);
                            mcAdapter.notifyDataSetChanged();
                        }catch (NumberFormatException nfe){
                            Toast.makeText(this, "Bad Key!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        try {
                            String theText = text.getText().toString();
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
}

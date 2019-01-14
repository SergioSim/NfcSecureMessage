package ans.mbds;

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

import database.Database;
import database.Message;
import utils.Logging;

public class ConversationActivity extends AppCompatActivity implements MessageCellAdapterListener{

    public static final String TAG = Logging.getTAG(ConversationActivity.class);

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private String contact;
    private Database db;
    private MessageCellAdapter mcAdapter = null;
    private Button btn;
    private EditText text;

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
        updateMessageList();
        mcAdapter = new MessageCellAdapter(this, messageList , this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(mcAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        btn = findViewById(R.id.messageSendButton);
        btn.setOnClickListener(v -> onClick());
        text = findViewById(R.id.messageText);
    }

    private void onClick() {
        String theText = text.getText().toString();
        Message message = new Message("Me", contact, contact, theText);
        db.addMessage(message);
        text.setText(null);
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMessageList();
        Log.i(TAG, "onResume Called! size of messageList: " + messageList.size());
        mcAdapter = new MessageCellAdapter(this, messageList , this);
        recyclerView.setAdapter(mcAdapter);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void updateMessageList() {
        messageList = db.getMessageByConversation(contact);
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
}

package ans.mbds;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import database.Message;
import utils.Logging;

public class ConversationActivity extends AppCompatActivity implements MessageCellAdapterListener{

    public static final String TAG = Logging.getTAG(ConversationActivity.class);

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageCellAdapter mcAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        messageList.add(new Message("a", "b", "hello"));
        messageList.add(new Message("b", "a", "b", "hello"));
        messageList.add(new Message("a", "b", "how is going?"));
        messageList.add(new Message("b", "a", "b", "bye"));

        messageList.add(new Message("a", "b", "hello"));
        messageList.add(new Message("b", "a", "b", "hello"));
        messageList.add(new Message("a", "b", "how is going?"));
        messageList.add(new Message("b", "a", "b", "bye"));

        messageList.add(new Message("a", "b", "really really really really really really really really really really really really really long message ..."));
        messageList.add(new Message("b", "a", "b", "hello"));
        messageList.add(new Message("a", "b", "how is going?"));
        messageList.add(new Message("b", "a", "b", "bye"));

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(mcAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.setAdapter(new MessageCellAdapter(this, messageList , this));
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void textClicked(Message message, MessageCellAdapter.MyViewHolder holder) {
        Log.i(TAG, "message clicked: " + message.getMessage());
    }

    @Override
    public boolean longtextClicked(Message message, MessageCellAdapter.MyViewHolder holder) {
        Log.i(TAG, "message long clicked: " + message.getMessage());
        return false;
    }
}

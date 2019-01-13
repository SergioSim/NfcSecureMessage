package ans.mbds;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import utils.Logging;

public class ConversationActivity extends AppCompatActivity {

    public static final String TAG = Logging.getTAG(ConversationActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
    }
}

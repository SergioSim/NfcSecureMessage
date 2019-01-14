package ans.mbds;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nfctools.Nfc;
import utils.Logging;

public class NFCReadFragment extends DialogFragment {

    public static final String TAG = Logging.getTAG(NFCReadFragment.class);

    public static NFCReadFragment newInstance() {
        return new NFCReadFragment();
    }

    private TextView mTvMessage;
    private Listener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read,container,false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mTvMessage = view.findViewById(R.id.tv_message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (Listener) context;
        mListener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onDialogDismissed();
    }

    public String onNfcDetected(Nfc mNfc){
        return readFromNFC(mNfc);
    }

    private String readFromNFC(Nfc mNfc) {
        String message = mNfc.read();
        Log.d(TAG, "readFromNFC: "+ message);
        mTvMessage.setText(message);
        return message;
    }
}

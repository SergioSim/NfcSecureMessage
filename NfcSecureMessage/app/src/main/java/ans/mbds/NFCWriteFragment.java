package ans.mbds;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import nfctools.Nfc;
import utils.Logging;

public class NFCWriteFragment extends DialogFragment {

    public static final String TAG = Logging.getTAG(NFCWriteFragment.class);

    public static NFCWriteFragment newInstance() {
        return new NFCWriteFragment();
    }

    private TextView mTvMessage;
    private ProgressBar mProgress;
    private Listener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write,container,false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

        mTvMessage = view.findViewById(R.id.tv_message);
        mProgress = view.findViewById(R.id.progress);
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

    public boolean onNfcDetected(Nfc mNfc, String messageToWrite){
        mProgress.setVisibility(View.VISIBLE);
        return writeToNfc(mNfc,messageToWrite);
    }

    private boolean writeToNfc(Nfc mNfc, String message){
        mTvMessage.setText(getString(R.string.message_write_progress));
        if(mNfc.write(message)){
            mTvMessage.setText(getString(R.string.message_write_success));
            mProgress.setVisibility(View.GONE);
            return true;
        }

        mTvMessage.setText(getString(R.string.message_write_error));
        return false;
    }
}
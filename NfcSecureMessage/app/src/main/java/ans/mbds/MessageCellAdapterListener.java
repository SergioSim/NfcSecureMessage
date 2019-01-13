package ans.mbds;

import database.Message;

public interface MessageCellAdapterListener {

    void textClicked(Message message, MessageCellAdapter.MyViewHolder holder);
    boolean longtextClicked(Message message, MessageCellAdapter.MyViewHolder holder);
}

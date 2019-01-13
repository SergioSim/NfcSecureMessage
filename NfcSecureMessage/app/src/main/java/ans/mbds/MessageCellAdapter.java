package ans.mbds;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import database.Message;

public class MessageCellAdapter extends RecyclerView.Adapter<MessageCellAdapter.MyViewHolder>{

        public class MyViewHolder extends RecyclerView.ViewHolder{

            public final TextView you;
            public final TextView me;
            final MessageCellAdapter mca;

            public MyViewHolder(View itemView, MessageCellAdapter mca) {
                super(itemView);
                me = itemView.findViewById(R.id.cell_me);
                you = itemView.findViewById(R.id.cell_you);
                this.mca = mca;
            }
        }

        private final List<Message> messageList;
        private LayoutInflater mInflater;
        private MessageCellAdapterListener tal;

        public MessageCellAdapter(Context context, List<Message> messageList, MessageCellAdapterListener tal) {
            mInflater = LayoutInflater.from(context);
            this.messageList = messageList;
            this.tal = tal;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = mInflater.inflate(R.layout.message_cell, parent, false);
            return new MyViewHolder(itemView, this);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Message message = messageList.get(position);
            int i = 1, z = -2;
            TextView[] textViews = {holder.me, holder.you};
            if(message.isMeTheAuthor()){i = 0; z = 0;}
            textViews[i].setText(message.getMessage());
            textViews[i + 1 + z].setBackgroundColor(Color.TRANSPARENT);
            textViews[i].setOnClickListener((view -> tal.textClicked(messageList.get(position), holder)));
            textViews[i].setOnLongClickListener((view -> tal.longtextClicked(messageList.get(position), holder)));
        }

        @Override
        public int getItemCount() { return messageList.size(); }

}

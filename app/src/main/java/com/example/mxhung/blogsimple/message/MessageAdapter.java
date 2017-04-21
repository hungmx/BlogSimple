package com.example.mxhung.blogsimple.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mxhung.blogsimple.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by MXHung on 4/13/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<MessageModel> listMessage;
    FirebaseAuth mAuth;
    String currentId = "";

    public static final int SENDER = 0;
    public static final int RECEIVER = 1;


    public MessageAdapter(Context context, ArrayList<MessageModel> listMessage) {
        this.context = context;
        this.listMessage = listMessage;
        mAuth = FirebaseAuth.getInstance();
        currentId = mAuth.getCurrentUser().getUid();
    }

    @Override
    public int getItemViewType(int position) {
        if ((listMessage.get(position).getSender()).equals(currentId)){
            return SENDER;
        }else {
            return RECEIVER;
        }
//        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case SENDER:
                View viewSender = inflater.inflate(R.layout.sender_message, parent, false);
                viewHolder = new ViewHolderSender(viewSender);
                break;
            case RECEIVER:
                View viewReceiver = inflater.inflate(R.layout.receiver_message, parent, false);
                viewHolder = new ViewHolderReceiver(viewReceiver);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //check
        switch (holder.getItemViewType()) {
            case SENDER:
                ViewHolderSender viewHolderSender = (ViewHolderSender) holder;
                MessageModel messageModel = listMessage.get(position);
                viewHolderSender.tvSenderMessage.setText(messageModel.getText());
                break;
            case RECEIVER:
                ViewHolderReceiver viewHolderReceiver = (ViewHolderReceiver) holder;
                MessageModel messageModel1 = listMessage.get(position);
                viewHolderReceiver.tvRecipientMessage.setText(messageModel1.getText());
                break;
        }

    }

    @Override
    public int getItemCount() {
        return (listMessage != null) ? listMessage.size() : 0;
    }

    public class ViewHolderSender extends RecyclerView.ViewHolder {

        public TextView tvSenderMessage;

        public ViewHolderSender(View itemView) {
            super(itemView);
            tvSenderMessage = (TextView) itemView.findViewById(R.id.tvSenderMessage);
        }
    }

    public class ViewHolderReceiver extends RecyclerView.ViewHolder {

        public TextView tvRecipientMessage;

        public ViewHolderReceiver(View itemView) {
            super(itemView);
            tvRecipientMessage = (TextView) itemView.findViewById(R.id.tvRecipientMessage);
        }
    }
}

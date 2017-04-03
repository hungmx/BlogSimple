package com.example.mxhung.blogsimple.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mxhung.blogsimple.R;

import java.util.ArrayList;

/**
 * Created by MXHung on 4/3/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    private Context context;
    private ArrayList<User> list;

    public UserAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Khoi tao view
        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_chat, parent, false);
        return new UserHolder(mView);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public class UserHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imAvatar;
        public UserHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }
}



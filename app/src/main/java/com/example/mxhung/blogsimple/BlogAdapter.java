package com.example.mxhung.blogsimple;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mxhung.blogsimple.model.Blog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by MXHung on 3/5/2017.
 */

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private Context context;
    private ArrayList<Blog> listBlog;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseBlog;
    private FirebaseAuth mAuth;
    private boolean progressLike = false;

    private Long like = 0l;

    public BlogAdapter(Context context, ArrayList<Blog> listBlog) {
        this.context = context;
        this.listBlog = listBlog;
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Like");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("User");
        mDatabaseBlog = FirebaseDatabase.getInstance().getReference().child("Blog");
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new BlogViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final BlogViewHolder holder, int position) {


        final Blog blog = listBlog.get(position);

        holder.tvTitle.setText(blog.getTitle());
        holder.tvDesc.setText(blog.getDescription());
        holder.tvName.setText(blog.getUsername());
        holder.tvTime.setText(blog.getTime());

        if (blog.getLike() == 0){
            holder.tvNumberLike.setVisibility(View.GONE);
        }else {
            holder.tvNumberLike.setVisibility(View.VISIBLE);
            holder.tvNumberLike.setText(String.valueOf(blog.getLike()));

        }

        if (!blog.getImage().equals("")) {
            holder.imPost.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(blog.getImage())
                    .error(R.drawable.no_image)
                    .skipMemoryCache(true)
                    .into(holder.imPost);

        } else {
            holder.imPost.setVisibility(View.GONE);
        }
        mDatabaseUser.child(blog.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String image = (String) dataSnapshot.child("image").getValue();
                Glide.with(context)
                        .load(image)
                        .error(R.drawable.no_image)
                        .skipMemoryCache(true)
                        .into(holder.imAvatar);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(blog.getKey()).hasChild(mAuth.getCurrentUser().getUid())) {
                    holder.imLike.setImageResource(R.drawable.like);
                } else {
                    holder.imLike.setImageResource(R.drawable.dislike);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.imPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String key = listBlog.get(position).getKey();
                Intent iDetail = new Intent(context, DetailPost.class);
                iDetail.putExtra("key", blog.getKey());
                v.getContext().startActivity(iDetail);
            }
        });
        holder.imLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseBlog.child(blog.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        like = (Long) dataSnapshot.child("like").getValue();
                        Log.d("likeBG", String.valueOf(like) + blog.getKey());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                progressLike = true;
//                String like;

                mDatabaseLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (progressLike) {
                            //check xem da co id cua bai viet chua, neu chua thi set value
                            if (dataSnapshot.child(blog.getKey()).hasChild(mAuth.getCurrentUser().getUid())) {
                                //neu co r, nghia la click lan 2,thi se xoa gia tri
                                mDatabaseLike.child(blog.getKey()).child(mAuth.getCurrentUser().getUid()).removeValue();
                                like--;

                                Log.d("likeSub", String.valueOf(like));
//                                        mDatabaseBlog.child(blog.getKey()).child("like").setValue(like);
                                holder.tvNumberLike.setText(String.valueOf(like));
                                mDatabaseBlog.child(blog.getKey()).child("like").setValue(like);

                                progressLike = false;


                            } else {
                                mDatabaseLike.child(blog.getKey()).child(mAuth.getCurrentUser().getUid()).setValue(blog.getTitle());
                                like++;
                                Log.d("likeAdd", String.valueOf(like));
//                                        mDatabaseBlog.child(blog.getKey()).child("like").setValue(like);
                                holder.tvNumberLike.setText(String.valueOf(like));
                                mDatabaseBlog.child(blog.getKey()).child("like").setValue(like);

                                progressLike = false;
                            }

                            if (like == 0){
                                holder.tvNumberLike.setVisibility(View.GONE);
                            }else {
                                holder.tvNumberLike.setVisibility(View.VISIBLE);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return (listBlog != null) ? listBlog.size() : 0;
    }

    public class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TextView tvTitle;
        public TextView tvDesc;
        public ImageView imPost;
        public TextView tvName;
        public TextView tvTime;
        public ImageButton imLike;
        public ImageView imAvatar;
        public TextView tvNumberLike;

        public BlogViewHolder(View itemView) {
            super(itemView);
//            itemView = mView;
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDes);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            imPost = (ImageView) itemView.findViewById(R.id.imPost);
            imLike = (ImageButton) itemView.findViewById(R.id.imLike);
            imAvatar = (ImageView) itemView.findViewById(R.id.imAvatar);
            tvNumberLike = (TextView) itemView.findViewById(R.id.tvNumberLike);
        }
    }
}

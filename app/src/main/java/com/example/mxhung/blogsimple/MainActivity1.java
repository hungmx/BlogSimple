package com.example.mxhung.blogsimple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mxhung.blogsimple.model.Blog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//hien thi cac bai Post
public class MainActivity1 extends AppCompatActivity {
    private RecyclerView rvListPost;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private boolean progressLike = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Blog").getRef();

        FirebaseRecyclerAdapter<Blog, BlogViewHolde> fbadapter =
                new FirebaseRecyclerAdapter<Blog, BlogViewHolde>(
                        Blog.class,
                        R.layout.item_post,
                        BlogViewHolde.class,
                        myRef
                ) {
                    @Override
                    protected void populateViewHolder(BlogViewHolde viewHolder, Blog model, final int position) {
                        final String post_key = getRef(position).getKey();
                        viewHolder.tvTitle.setText(model.getTitle());
                        viewHolder.tvDesc.setText(model.getDescription());
                        viewHolder.tvName.setText(model.getUsername());
                        Glide.with(MainActivity1.this)
                                .load(model.getImage())
                                .into(viewHolder.imPost);

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity1.this, post_key, Toast.LENGTH_SHORT).show();
                            }
                        });

                        viewHolder.imLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progressLike = true;

                                if (progressLike){

                                }

                            }
                        });
                    }
                };

        rvListPost = (RecyclerView) findViewById(R.id.rvListPost);
        rvListPost.setHasFixedSize(true);
        rvListPost.setLayoutManager(new LinearLayoutManager(this));


        rvListPost.setAdapter(fbadapter);
    }


    public static class BlogViewHolde extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDesc;
        ImageView imPost;
        TextView tvName;
        ImageButton imLike;

        public BlogViewHolde(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDes);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            imPost = (ImageView) itemView.findViewById(R.id.imPost);
            imLike = (ImageButton) itemView.findViewById(R.id.imLike);
        }
    }
}

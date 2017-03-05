package com.example.mxhung.blogsimple;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mxhung.blogsimple.model.Blog;

import java.util.ArrayList;

/**
 * Created by MXHung on 3/5/2017.
 */

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder>{
    private Context context;
    private ArrayList<Blog> listBlog;

    public BlogAdapter(Context context, ArrayList<Blog> listBlog) {
        this.context = context;
        this.listBlog = listBlog;
    }

    @Override
    public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new BlogViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(BlogViewHolder holder, int position) {
        Blog blog = listBlog.get(position);
        holder.tvTitle.setText(blog.getTitle());
        holder.tvDesc.setText(blog.getDesc());
        Glide.with(context)
                .load(blog.getImage())
                .error(R.drawable.no_image)
                .skipMemoryCache(true)
                .into(holder.imPost);
    }

    @Override
    public int getItemCount() {
        return (listBlog != null) ? listBlog.size() : 0;
    }

    public class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public TextView tvTitle;
        public TextView tvDesc;
        public ImageView imPost;
        public BlogViewHolder(View itemView) {
            super(itemView);
//            itemView = mView;
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDesc = (TextView) itemView.findViewById(R.id.tvDes);
            imPost = (ImageView) itemView.findViewById(R.id.imPost);

        }
    }
}

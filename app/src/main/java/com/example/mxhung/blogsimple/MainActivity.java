package com.example.mxhung.blogsimple;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mxhung.blogsimple.model.Blog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//hien thi cac bai Post
public class MainActivity extends AppCompatActivity {
    private RecyclerView rvListPost;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private ArrayList<Blog> listBlog;
    BlogAdapter adapter = null;
    private final String TAG = "Main";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabaseBlog;
    private boolean progressLike = false;

    private DatabaseReference mDatabaseLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvListPost = (RecyclerView) findViewById(R.id.rvListPost);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("User");
        mDatabaseBlog = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseBlog = FirebaseDatabase.getInstance().getReference().child("Like");
        mDatabase.keepSynced(true);
        mDatabaseUser.keepSynced(true);


        listBlog = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //check xem da co tai khoan nao chua
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent iLogin = new Intent(MainActivity.this, LoginActivity.class);
                    iLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(iLogin);
                }
            }
        };


//        adapter = new BlogAdapter(getApplicationContext(), listBlog);
//        Log.d(TAG + "--adapter", String.valueOf(adapter));
//        rvListPost.setHasFixedSize(true);
//        rvListPost.setLayoutManager(new LinearLayoutManager(this));
//        rvListPost.setItemAnimator(new DefaultItemAnimator());
//        rvListPost.setAdapter(adapter);

        loadData();

//        mDatabase.child("Blog").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                //doc du lieu ra tu nut con Blog
//                Blog blog = dataSnapshot.getValue(Blog.class);
//                String sss = dataSnapshot.getKey();
//                Log.d("---", sss);
//                //phải để tên title , description, image trùng vs trên firebase
//                Blog added = new Blog(blog.title, blog.description, blog.image, blog.username);
//                listBlog.add(added);
//                Log.d(TAG + "--listBlog", String.valueOf(listBlog));
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        checkUserExist();


//        rvListPost.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), rvListPost, new RecyclerTouchListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));
    }

    private void loadData() {
        FirebaseRecyclerAdapter<Blog, BlogViewHolde> fbadapter =
                new FirebaseRecyclerAdapter<Blog, BlogViewHolde>(
                        Blog.class,
                        R.layout.item_post,
                        BlogViewHolde.class,
                        mDatabaseBlog
                ) {
                    @Override
                    protected void populateViewHolder(final BlogViewHolde viewHolder, Blog model, final int position) {
                        final String post_key = getRef(position).getKey();
                        viewHolder.tvTitle.setText(model.getTitle());
                        viewHolder.tvDesc.setText(model.getDescription());
                        viewHolder.tvName.setText(model.getUsername());
                        Glide.with(MainActivity.this)
                                .load(model.getImage())
                                .into(viewHolder.imPost);

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, post_key, Toast.LENGTH_SHORT).show();
                            }
                        });

                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                                    viewHolder.imLike.setImageResource(R.drawable.like);
                                }else {
                                    viewHolder.imLike.setImageResource(R.drawable.dislike);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        viewHolder.imLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progressLike = true;

                                mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (progressLike) {
                                            //check xem da co id cua bai viet chua, neu chua thi set value
                                            if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                                //neu co r, nghia la click lan 2,thi se xoa gia tri
                                                mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                progressLike = false;


                                            } else {
                                                mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                                progressLike = false;
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
                };

        rvListPost = (RecyclerView) findViewById(R.id.rvListPost);
        rvListPost.setHasFixedSize(true);
        rvListPost.setLayoutManager(new LinearLayoutManager(this));
        rvListPost.setItemAnimator(new DefaultItemAnimator());


        rvListPost.setAdapter(fbadapter);
        fbadapter.notifyDataSetChanged();
    }

    private void checkUserExist() {
        if (mAuth.getCurrentUser() != null) {
            final String userId = mAuth.getCurrentUser().getUid();
            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //neu o DB k co userId thi se sang man hinh cai dat user
                    Boolean uid = dataSnapshot.hasChild(userId);
                    Log.d("--uid", uid + "");
                    if (!dataSnapshot.hasChild(userId)) {
                        Intent iSetting = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(iSetting);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ic_post) {
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }

        if (item.getItemId() == R.id.action_logout) {
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        mAuth.signOut();
    }

    public class BlogViewHolde extends RecyclerView.ViewHolder {
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

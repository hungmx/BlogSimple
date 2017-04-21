package com.example.mxhung.blogsimple;

import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mxhung.blogsimple.model.Blog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WallActivity extends AppCompatActivity {
    private RecyclerView rvListPost;
    private DatabaseReference mDatabase;
    private Query mQueryDatabase;
    private DatabaseReference mDatabaseUser;
    private ArrayList<Blog> listBlog;
    private BlogAdapter adapter = null;
    private final String TAG = "Main";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabaseBlog;
    private boolean progressLike = false;

    private DatabaseReference mDatabaseLike;
    @BindView(R.id.imAvatar)
    ImageView imAvatar;
    @BindView(R.id.tvName)
    TextView tvName;

    private String name;
    private String image;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);
        init();

        checkCurrentUser();

        getData();

        setAdapter();

//        checkUserSetting();

    }

    private void init() {
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        rvListPost = (RecyclerView) findViewById(R.id.rvListPost);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mQueryDatabase = mDatabase.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("User");
        mDatabaseBlog = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Like");
        mDatabase.keepSynced(true);
        mDatabaseUser.keepSynced(true);
        mDatabaseBlog.keepSynced(true);
        mDatabaseLike.keepSynced(true);

        listBlog = new ArrayList<>();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Waitting...");
        dialog.show();
    }


    private void setAdapter() {
        adapter = new BlogAdapter(getApplicationContext(), listBlog);
        Log.d(TAG + "--adapter", String.valueOf(adapter));
        rvListPost.setHasFixedSize(true);
        rvListPost.setLayoutManager(new LinearLayoutManager(this));
        rvListPost.setItemAnimator(new DefaultItemAnimator());
        rvListPost.setAdapter(adapter);


    }

    private void getData() {
        //get data
        mQueryDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //doc du lieu ra tu nut con Blog
                Blog blog = dataSnapshot.getValue(Blog.class);
//                String sss = dataSnapshot.getKey();
//                Log.d("---", sss);
                //phải để tên title , description, image trùng vs trên firebase
                Blog added = new Blog(blog.key, blog.title, blog.description, blog.image, blog.username, blog.uid, blog.time, blog.like);
                listBlog.add(added);
                Log.d(TAG + "--listBlog", String.valueOf(listBlog));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void checkCurrentUser() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //check xem dang nhap
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    String uid = user.getUid();
                    mDatabaseUser.child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            name = (String) dataSnapshot.child("name").getValue();
                            image = (String) dataSnapshot.child("image").getValue();
                            Glide.with(getApplicationContext())
                                    .load(image)
                                    .error(R.drawable.no_image)
                                    .into(imAvatar);
                            tvName.setText(name);

                            dialog.dismiss();
//                            Toast.makeText(WallActivity.this, "Chào mừng bạn đến với Blog Bear", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            dialog.dismiss();
                        }
                    });

                } else {
                    // User is signed out
                    Intent iLogin = new Intent(WallActivity.this, LoginActivity.class);
                    iLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(iLogin);
                }
            }
        };
    }

    @OnClick(R.id.btStatus)
    public void status(){
        Intent iPost = new Intent(WallActivity.this, PostActivity.class);
        iPost.putExtra("avatar", image);
        startActivity(iPost);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }




    @Override
    protected void onStart() {
        super.onStart();
        //lang nghe su kien thay doi nhu logout,login
        //neu k co la logout xong k nhan dc su kien
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }
}

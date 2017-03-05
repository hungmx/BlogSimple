package com.example.mxhung.blogsimple;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mxhung.blogsimple.model.Blog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

//hien thi cac bai Post
public class MainActivity extends AppCompatActivity {
    private RecyclerView rvListPost;
    private DatabaseReference mDatabase;
    private ArrayList<Blog> listBlog;
    BlogAdapter adapter = null;
    private final String TAG = "Main";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvListPost = (RecyclerView) findViewById(R.id.rvListPost);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listBlog = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //check xem da co tai khoan nao chua
                if (firebaseAuth.getCurrentUser() == null){
                    Intent iLogin = new Intent(MainActivity.this, RegisterActivity.class);
                    iLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(iLogin);
                }
            }
        };


        adapter = new BlogAdapter(getApplicationContext(), listBlog);
        Log.d(TAG + "--adapter", String.valueOf(adapter));
        rvListPost.setHasFixedSize(true);
        rvListPost.setLayoutManager(new LinearLayoutManager(this));
        rvListPost.setItemAnimator(new DefaultItemAnimator());
        rvListPost.setAdapter(adapter);

        mDatabase.child("Blog").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //doc du lieu ra tu nut con Blog
                Blog blog = dataSnapshot.getValue(Blog.class);
                //phải để tên title , description, image trùng vs trên firebase
                Blog added = new Blog(blog.title, blog.description, blog.image);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ic_post){
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }

        if (item.getItemId() == R.id.action_logout){
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        mAuth.signOut();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}

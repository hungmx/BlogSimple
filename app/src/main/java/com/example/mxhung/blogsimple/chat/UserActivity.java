package com.example.mxhung.blogsimple.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.mxhung.blogsimple.R;
import com.example.mxhung.blogsimple.RecyclerTouchListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MXHung on 4/3/2017.
 */

public class UserActivity extends AppCompatActivity {
    @BindView(R.id.rvListUser)
    RecyclerView rvListUser;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseRoom;
    private ArrayList<User> listUser;
    private UserAdapter adapter;
    private ArrayList<String> listRoom;
    private FirebaseAuth mAuth;
    String roomId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ButterKnife.bind(this);
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("User");
        mDatabaseRoom = FirebaseDatabase.getInstance().getReference().child("Room");
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser.keepSynced(true);
        listUser = new ArrayList<>();
        listRoom = new ArrayList<>();

        getData();

        setAdapter();

        rvListUser.addOnItemTouchListener(new RecyclerTouchListener(this, rvListUser, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Intent iChat = new Intent(UserActivity.this, ChatActivity.class);
                iChat.putExtra("uId", listUser.get(position).getuId());
                Log.d("--uId", listUser.get(position).getuId());

//                final String[] roomId = {""};


                Log.d("--roomId2", roomId);
                if (!roomId.equals("")) {
                    iChat.putExtra("roomId", roomId);
                } else {
                    iChat.putExtra("roomId", "");
                }
                startActivity(iChat);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private void setAdapter() {
        adapter = new UserAdapter(getApplicationContext(), listUser);
        rvListUser.setHasFixedSize(true);
        rvListUser.setLayoutManager(new LinearLayoutManager(this));
        rvListUser.setItemAnimator(new DefaultItemAnimator());
        rvListUser.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getData() {
        mDatabaseUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);

                User userAdded = new User(user.name, user.image, user.uId);
                listUser.add(userAdded);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }
}

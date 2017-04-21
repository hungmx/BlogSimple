package com.example.mxhung.blogsimple.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.mxhung.blogsimple.R;
import com.example.mxhung.blogsimple.message.MessageAdapter;
import com.example.mxhung.blogsimple.message.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MXHung on 4/4/2017.
 */

public class ChatActivity extends AppCompatActivity {
    @BindView(R.id.rvListChat)
    RecyclerView rvListChat;
    @BindView(R.id.etMessage)
    EditText etMessage;
    DatabaseReference mDatabaseMessage;
    DatabaseReference mDatabaseRoom;
    DatabaseReference mDatabaseUser;
    private String roomId = "";
    private String uId = "";
    FirebaseAuth mAuth;
    String currentId;

    private ArrayList<MessageModel> listMessage;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDatabaseMessage = FirebaseDatabase.getInstance().getReference().child("Message");
        mDatabaseRoom = FirebaseDatabase.getInstance().getReference().child("Room");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("User");
        mAuth = FirebaseAuth.getInstance();
        uId = getIntent().getStringExtra("uId");
        currentId = mAuth.getCurrentUser().getUid();

        listMessage = new ArrayList<>();
        getData();
        setAdapter();

    }

    private void getData() {
        mDatabaseUser.child(currentId).child("room").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uId)) {
                    String roomId = dataSnapshot.child(uId).getValue().toString();
                    mDatabaseMessage.child(roomId).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                            MessageModel message = new MessageModel(messageModel.time, messageModel.text, messageModel.sender);
                            listMessage.add(message);
                            //scroll toi vi tri chat ket thuc cuoi cung
                            rvListChat.scrollToPosition(adapter.getItemCount()-1);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setAdapter() {
        Log.d("--listMessage", String.valueOf(listMessage.size()));
        adapter = new MessageAdapter(getApplicationContext(), listMessage);
        rvListChat.setHasFixedSize(true);
        rvListChat.setLayoutManager(new LinearLayoutManager(this));
        rvListChat.setItemAnimator(new DefaultItemAnimator());
        rvListChat.setAdapter(adapter);
    }


    @OnClick(R.id.btSendMessage)
    public void sendMessage() {
        //check xem trong room cua user co uid cua ng nhan hay chua
        mDatabaseUser.child(currentId).child("room").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(uId)) {
                    //room
                    DatabaseReference newRoom = mDatabaseRoom.push();
                    String key = newRoom.getKey();
                    newRoom.setValue(key);
                    roomId = key;
                    //user
                    mDatabaseUser.child(uId).child("room").child(currentId).setValue(key);
                    mDatabaseUser.child(currentId).child("room").child(uId).setValue(key);

                } else {
                    String text = etMessage.getText().toString();
                    roomId = dataSnapshot.child(uId).getValue().toString();
                    //message
//                    DatabaseReference newMessage = mDatabaseMessage.child(roomId).push();
//                    newMessage.child("time").setValue("123456");
//                    newMessage.child("text").setValue(text);
//                    newMessage.child("sender").setValue(currentId);

                    mDatabaseMessage.child(roomId).push().setValue(new MessageModel("1234", text, currentId));

                    etMessage.setText("");

                }
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

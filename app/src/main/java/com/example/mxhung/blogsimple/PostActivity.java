package com.example.mxhung.blogsimple;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MXHung on 3/5/2017.
 */

public class PostActivity extends AppCompatActivity {
    private final String TAG = "post";
    @BindView(R.id.imPost)
    ImageView imPost;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.imAvatar)
    ImageView imAvatar;
    private Button btPost;
    private static final int GALLERY_REQUEST = 1;
    String image = "";

    private Uri imageUri = null;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private ProgressDialog dialog;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDBUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        btPost = (Button) findViewById(R.id.btPost);
        mStorage = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        //truy cap toi nut con cua User la Uid
        mDBUser = FirebaseDatabase.getInstance().getReference().child("User").child(mCurrentUser.getUid());
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        dialog = new ProgressDialog(this);

        image = getIntent().getStringExtra("avatar");
        Glide.with(getApplicationContext())
                .load(image)
                .error(R.drawable.no_image)
                .into(imAvatar);
//        imPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //mo den thu vien
//                Intent iGallery = new Intent(Intent.ACTION_GET_CONTENT);
//                iGallery.setType("image/*");
//                startActivityForResult(iGallery, GALLERY_REQUEST);
//            }
//        });

        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPost();
            }
        });
    }

    @OnClick(R.id.btAddImage)
    public void addImage() {
        imPost.setVisibility(View.VISIBLE);
        //mo den thu vien
        Intent iGallery = new Intent(Intent.ACTION_GET_CONTENT);
        iGallery.setType("image/*");
        startActivityForResult(iGallery, GALLERY_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            imPost.setImageURI(imageUri);
        }else {
            imageUri = Uri.parse("");
        }
    }

    private void startPost() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = new Date();
        final String time = dateFormat.format(date);

        final String title = etTitle.getText().toString().trim();
        final String description = etDescription.getText().toString().trim();

        if (!TextUtils.isEmpty(description)) {
            dialog.setMessage("Dang post...");
            dialog.show();

            if (imageUri != null) {
                StorageReference filepath = mStorage.child("Blog_Images").child(imageUri.getLastPathSegment());
                Log.d(TAG + "--imageUri", imageUri.getLastPathSegment());
                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d(TAG + "--downloadUrl", String.valueOf(downloadUrl));

                        //luu mỗi bài post vao database cac bai dang vao mDatabase muc Blog
                        final DatabaseReference newPost = mDatabase.push();
                        //su dung User de sau lay name cua user post
                        mDBUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String key = newPost.getKey();

                                newPost.child("key").setValue(key);
                                newPost.child("title").setValue(title);
                                newPost.child("description").setValue(description);
                                if (imageUri != null) {
                                    newPost.child("image").setValue(downloadUrl.toString());
                                } else {
                                    newPost.child("image").setValue("");
                                }
                                newPost.child("time").setValue(time);
                                newPost.child("uid").setValue(mCurrentUser.getUid());
                                newPost.child("like").setValue(0);
                                //lay gia tri name tu chid Uid
                                newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(PostActivity.this, MainActivity.class));
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        dialog.dismiss();
                        Toast.makeText(PostActivity.this, "Post bai thanh cong", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {

                //luu mỗi bài post vao database cac bai dang vao mDatabase muc Blog
                final DatabaseReference newPost = mDatabase.push();
                //su dung User de sau lay name cua user post
                mDBUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String key = newPost.getKey();

                        newPost.child("key").setValue(key);
                        newPost.child("title").setValue(title);
                        newPost.child("description").setValue(description);
                        newPost.child("time").setValue(time);
                        newPost.child("uid").setValue(mCurrentUser.getUid());
                        newPost.child("image").setValue("");
                        newPost.child("like").setValue(0);
                        //lay gia tri name tu chid Uid
                        newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                dialog.dismiss();
                Toast.makeText(PostActivity.this, "Post bai thanh cong", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Moi nhap thong tin bai dang", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
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

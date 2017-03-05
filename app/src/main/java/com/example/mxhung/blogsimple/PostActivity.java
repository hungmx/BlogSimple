package com.example.mxhung.blogsimple;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by MXHung on 3/5/2017.
 */

public class PostActivity extends AppCompatActivity {
    private final String TAG = "post";
    private ImageView imPost;
    private EditText etTitle;
    private EditText etDescription;
    private Button btPost;
    private static final int GALLERY_REQUEST = 1;

    private Uri imageUri = null;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        imPost = (ImageView) findViewById(R.id.imPost);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etDescription = (EditText) findViewById(R.id.etDescription);
        btPost = (Button) findViewById(R.id.btPost);
        mStorage = FirebaseStorage.getInstance().getReference();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        dialog = new ProgressDialog(this);

        imPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mo den thu vien
                Intent iGallery = new Intent(Intent.ACTION_GET_CONTENT);
                iGallery.setType("image/*");
                startActivityForResult(iGallery, GALLERY_REQUEST);
            }
        });

        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPost();
            }
        });
    }

    private void startPost() {
        dialog.setMessage("Dang post...");
        dialog.show();

        final String title = etTitle.getText().toString().trim();
        final String description = etDescription.getText().toString().trim();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && imageUri != null){
            StorageReference filepath = mStorage.child("Blog_Images").child(imageUri.getLastPathSegment());
            Log.d(TAG + "--imageUri", imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d(TAG + "--downloadUrl", String.valueOf(downloadUrl));

                    //luu mỗi bài post vao database cac bai dang vao mDatabase muc Blog
                    DatabaseReference newPost = mDatabase.push();
                    newPost.child("title").setValue(title);
                    newPost.child("description").setValue(description);
                    newPost.child("image").setValue(downloadUrl.toString());


                    Toast.makeText(PostActivity.this, "Post bai thanh cong", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    startActivity(new Intent(PostActivity.this, MainActivity.class));;
                }
            });
        }else {
            Toast.makeText(this, "Moi nhap thong tin bai dang", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            imPost.setImageURI(imageUri);
        }
    }
}

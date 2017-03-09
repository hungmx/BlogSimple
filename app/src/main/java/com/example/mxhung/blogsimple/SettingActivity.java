package com.example.mxhung.blogsimple;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {
    @BindView(R.id.imAvatar)
    ImageButton imAvatar;
    @BindView(R.id.etName)
    EditText etName;
    private static final int GALLERY_REQUEST = 1;
    private Uri imageUri = null;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        mAuth = FirebaseAuth.getInstance();
        //them nut con image
        mStorage = FirebaseStorage.getInstance().getReference().child("Profile_image");
        dialog = new ProgressDialog(this);
    }

    @OnClick(R.id.imAvatar)
    public void getImage(){
        Intent gallerIntent = new Intent();
        gallerIntent.setAction(Intent.ACTION_GET_CONTENT);
        gallerIntent.setType("image/*");
        startActivityForResult(gallerIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //Xet ti le 1 1
                    .setAspectRatio(1,1)
                    .start(this);

        }

        //nhan ket qua tra ve sau khi crop
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                imAvatar.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @OnClick(R.id.btSetting)
    public void setting(){
        final String name = etName.getText().toString().trim();
        final String uID = mAuth.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(name) && imageUri != null){
            dialog.setMessage("Setting...");
            dialog.show();

            StorageReference filepath = mStorage.child(imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDatabase.child(uID).child("name").setValue(name);
                    mDatabase.child(uID).child("image").setValue(downloadUri);
                    dialog.dismiss();

                    Intent iMain = new Intent(SettingActivity.this, MainActivity.class);
                    startActivity(iMain);
                }
            });
        }
        }

}

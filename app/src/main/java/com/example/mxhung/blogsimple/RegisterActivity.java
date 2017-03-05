package com.example.mxhung.blogsimple;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    String email, name, password;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;
    private ProgressDialog dialog;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
        //tao moi 1 nut User
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        dialog = new ProgressDialog(this);
    }

    @OnClick(R.id.btSignUp)
    public  void signUp(){
        dialog.setMessage("Signing Up...");
        dialog.show();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    AuthResult authResult = task.getResult();
                    Log.d("authResult", String.valueOf(authResult));
                    String user_id = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_id = mDatabase.child(user_id);
                    //them nut user_id cac thuoc tinh
                    current_user_id.child("name").setValue(name);
                    current_user_id.child(password).setValue(password);
                    current_user_id.child("image").setValue("default");

                    Intent iMain = new Intent(RegisterActivity.this, MainActivity.class);
                    iMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(iMain);
                    dialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Ban da tao thanh cong", Toast.LENGTH_SHORT).show();

                }else {
                    dialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Ban da tao that bai", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

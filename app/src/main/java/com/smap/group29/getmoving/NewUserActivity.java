package com.smap.group29.getmoving;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smap.group29.getmoving.model.User;

public class NewUserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageView iv_userImage;
    private EditText et_email,et_password,et_name, et_age, et_city, et_dailySteps;
    private Button btn_cancel, btn_save, btn_addPhoto;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        initUI();
        mAuth = FirebaseAuth.getInstance();

        //If the current user is logged in already we'll send them to the UserActivity
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), UserActivity.class));
            finish();
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    et_email.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    et_password.setError("Password is required");
                    return;
                }
                if(password.length() < 6){
                    et_password.setError("Password must be >= 6 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //register user in fb
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(NewUserActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), UserActivity.class));
                            finish();
                        }else{
                            Toast.makeText(NewUserActivity.this, "Error" + task.getException(),Toast.LENGTH_SHORT).show();

                        }


                    }
                });

            }
        });


    }

    private void initUI(){
        iv_userImage    = findViewById(R.id.iv_newUserImage);
        btn_addPhoto    = findViewById(R.id.btn_addPhoto);
        et_email        = findViewById(R.id.et_email);
        et_password     = findViewById(R.id.et_password);
        et_name         = findViewById(R.id.et_name);
        et_age          = findViewById(R.id.et_age);
        et_city         = findViewById(R.id.et_city);
        et_dailySteps   = findViewById(R.id.et_dailygoal);
        btn_save        = findViewById(R.id.btn_save);
        btn_cancel      = findViewById(R.id.btn_cancel);
        progressBar = findViewById(R.id.progressBar);
    }



}

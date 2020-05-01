package com.smap.group29.getmoving;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smap.group29.getmoving.Activities.NewUserActivity;
import com.smap.group29.getmoving.Activities.UserActivity;
import com.smap.group29.getmoving.Utils.FirebaseUtil;

public class LoginActivity extends AppCompatActivity{

    private static FirebaseUtil firebaseUtil;
    private static FirebaseDatabase mFirebaseDatabase;
    private static DatabaseReference mDatabaseReference;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    private static Activity caller;

    // for storage of image
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageReference;



    private static final String TAG = "LoginActivity";

    private EditText et_email, et_password;
    private TextView tv_login, tv_createUser;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        initUI();
        Intent newAccountIntent = new Intent(this,NewUserActivity.class);

        setUI();
    }



    private void initUI(){
//        et_email        = findViewById(R.id.et_email);
//        et_password     = findViewById(R.id.et_password);
//        tv_login        = findViewById(R.id.tv_login);
//        tv_createUser   = findViewById(R.id.tv_createUser);
//        progressBar     = findViewById(R.id.progressBar2);
    }

    private void setUI(){
        tv_login.setOnClickListener(new View.OnClickListener() {
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

                //authenticate the user

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Logged in succesfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), UserActivity.class));

                        }else{
                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });


        tv_createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, NewUserActivity.class);
                startActivity(intent);


            }

        });
    }

    public static void openFirebaseReference(String ref, final Activity callerActivity){
        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;

            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    // check if the user is logged in else don't need to
                    if (firebaseAuth.getInstance().getCurrentUser() == null ){ FirebaseUtil.signIn(); }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                }
            };
        }
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }
}

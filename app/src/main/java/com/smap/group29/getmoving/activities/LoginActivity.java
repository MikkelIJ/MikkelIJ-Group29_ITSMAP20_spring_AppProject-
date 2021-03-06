package com.smap.group29.getmoving.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.utils.CheckPermissions;

public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";

    private EditText et_email, et_password;
    private TextView tv_login, tv_createUser;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private CheckPermissions mCheckPermissions;






    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        // If the current user is logged in already we'll send them to the UserActivity
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), UserActivity.class));
            finish();
        }
        initUI();
        setUI();
        // If the current user is logged in already we'll send them to the UserActivity
    }

    @Override
    protected void onStart() {
        super.onStart();

        // The CheckPermissions method has an issue that does not allow it to run on android versions < android 10
        // this might be due to the use of Snackbar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        mCheckPermissions = new CheckPermissions();
            mCheckPermissions.checkPermissions(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mCheckPermissions = new CheckPermissions();
            mCheckPermissions.checkPermissions(this);
        }

    }

    private void initUI(){
        et_email        = findViewById(R.id.et_email);
        et_password     = findViewById(R.id.et_password);
        tv_login        = findViewById(R.id.tv_login);
        tv_createUser   = findViewById(R.id.tv_createUser);
        progressBar     = findViewById(R.id.progressBar2);
    }

    private void setUI(){
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                //Set up requirements
                if(TextUtils.isEmpty(email)){
                    et_email.setError(getString(R.string.email_required));
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    et_password.setError(getString(R.string.password_required));
                    return;
                }
                if(password.length() < 6){
                    et_password.setError(getString(R.string.password_length));
                    return;
                }

               progressBar.setVisibility(View.VISIBLE);

                //authenticate the user

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, R.string.logged_in_succes, Toast.LENGTH_SHORT).show();
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
}

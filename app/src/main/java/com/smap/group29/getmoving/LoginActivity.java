package com.smap.group29.getmoving;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.smap.group29.getmoving.model.User;

public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";

    private EditText et_email;
    private EditText et_password;
    private TextView tv_login;
    private TextView tv_createUser;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference myDBRef;
    private User newUser;

    // new user in db inspired by https://www.youtube.com/watch?v=0gNPX52o_7I
    public static final String USER = "user";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseDatabase.getInstance();
        myDBRef = db.getReference(USER);
        mAuth = FirebaseAuth.getInstance();
        initUI();
        Intent newAccountIntent = new Intent(this,NewUserActivity.class);
        setUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        Log.v("login",currentUser.getEmail());
    }

    private void RegisterUser(final String email, final String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            createProfile();

                        }else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );
    }

    private void createProfile(){
        String keyId = myDBRef.push().getKey();
        myDBRef.child(keyId).setValue(newUser);
        startActivity(new Intent(LoginActivity.this,NewUserActivity.class));

    }

    private void signin(final String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(LoginActivity.this,UserActivity.class);
                            intent.putExtra("email",email);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void initUI(){
        et_email        = findViewById(R.id.et_email);
        et_password     = findViewById(R.id.et_password);
        tv_login        = findViewById(R.id.tv_login);
        tv_createUser   = findViewById(R.id.tv_createUser);
    }

    private void setUI(){
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_email.getText().toString() != "" && et_password.getText().toString() != ""){
                    signin(et_email.getText().toString(),et_password.getText().toString());
                }else {
                    Toast.makeText(LoginActivity.this,"Fill out Email and Password",Toast.LENGTH_LONG).show();
                }
            }
        });
        tv_createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_email.getText().toString() != "" && et_password.getText().toString() != ""){
                    newUser = new User(et_email.getText().toString(),et_password.getText().toString());
                    RegisterUser(et_email.getText().toString(),et_password.getText().toString());
                }else {
                    Toast.makeText(LoginActivity.this,"Fill out Email and Password",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}

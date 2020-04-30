package com.smap.group29.getmoving;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    private static final String TAG = "NewUserActivity";

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference myDBRef;
    private User newUser;
    public static final String USER = "user";
    public static String KEYID = "";

    private TextView tv_createNewUser_header;
    private ImageView iv_userImage;
    private Button btn_addPhoto;
    private EditText et_email;
    private EditText et_password;
    private EditText et_name;
    private EditText et_age;
    private EditText et_city;
    private EditText et_dailySteps;
    private Button btn_cancel;
    private Button btn_save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        initUI();
        db = FirebaseDatabase.getInstance();
        myDBRef = db.getReference(USER);
        mAuth = FirebaseAuth.getInstance();
        setUI();


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void initUI(){
        iv_userImage    = findViewById(R.id.iv_newUserImage);
        btn_addPhoto    = findViewById(R.id.btn_newUserAddPhoto);
        et_email        = findViewById(R.id.et_newUserEmail);
        et_password     = findViewById(R.id.et_newUserPassword);
        et_name         = findViewById(R.id.et_newUserName);
        et_age          = findViewById(R.id.et_newUserAge);
        et_city         = findViewById(R.id.et_newUserCity);
        et_dailySteps   = findViewById(R.id.userActivityDailySteps);
        btn_save        = findViewById(R.id.btn_newUserSave);
        btn_cancel      = findViewById(R.id.btn_newUserCancel);
    }

    private void setUI(){
        btn_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inspired by https://stackoverflow.com/questions/43246402/how-to-set-image-in-imageview-from-camera-and-gallery
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //readDatabase();
                if (!et_email.getText().toString().isEmpty() && !et_password.getText().toString().isEmpty() && !et_name.getText().toString().isEmpty() && !et_age.getText().toString().isEmpty() && !et_city.getText().toString().isEmpty() && !et_dailySteps.getText().toString().isEmpty()){
                    newUser = new User(et_email.getText().toString(), et_password.getText().toString(), et_name.getText().toString(),et_age.getText().toString(),et_city.getText().toString(),et_dailySteps.getText().toString());
                    RegisterUser(newUser.getEmail(),newUser.getPassword());
                }else {
                    Toast.makeText(NewUserActivity.this, "Fill out all values to save",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                            String keyId = myDBRef.push().getKey();
                            myDBRef.child(keyId).setValue(newUser);
                            KEYID = keyId;
                            signin(email,password);

                        }else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(NewUserActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );
    }

    private void createProfile(){
        String keyId = myDBRef.push().getKey();
        myDBRef.child(keyId).setValue(newUser);
        startActivity(new Intent(NewUserActivity.this,UserActivity.class));

    }

    private void signin(final String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(NewUserActivity.this,UserActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(NewUserActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    iv_userImage.setImageBitmap(imageBitmap);
                    break;
                }
        }
    }

    private void readDatabase(){
        myDBRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

}

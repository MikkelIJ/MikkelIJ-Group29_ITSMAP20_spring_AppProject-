package com.smap.group29.getmoving.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.model.NewUser;
import com.smap.group29.getmoving.utils.GlobalConstants;

import java.util.HashMap;
import java.util.Map;

public class NewUserActivity extends AppCompatActivity {
    //Newuser and Useractivity is inspired by  https://www.youtube.com/playlist?list=PLlGT4GXi8_8dDK5Y3KCxuKAPpil9V49rN

    public static final String TAG = "TAG";
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    StorageReference storageReference;
    String userID;

    Uri imageUri;
    Uri userUri;

    private NewUser newUser;

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
        mStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        iv_userImage.setImageResource(R.drawable.noprofilepicture);

        btn_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When user clicks on addphoto the app opens the camararoll
                Intent openCamararollIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openCamararollIntent, 101);

                //Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(takePicture, 0);
            }
        });



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = et_email.getText().toString().trim();
                final String password = et_password.getText().toString().trim();
                final String name = et_name.getText().toString();
                final String age = et_age.getText().toString();
                final String city = et_city.getText().toString();
                final String dailyGoal = et_dailySteps.getText().toString();


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
                            //getting the id of the currently logged in user
                            //creating new document and storing the data with hashmap
                            final String userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = mStore.collection(GlobalConstants.FIREBASE_USER_COLLECTION).document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("uID",mAuth.getUid());
                            user.put("email",email);
                            user.put("password", password);
                            user.put("name", name);
                            user.put("age", age);
                            user.put("city", city);
                            user.put("dailysteps","0");
                            user.put("dailygoal",dailyGoal);
                            user.put("stepstotal","0");
                            user.put("wins","0");
                            user.put("follow","0");
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"onSucces: user profile is created for" +userID);
                                    Log.v("signup",userID);
                                    startActivity(new Intent(getApplicationContext(), UserActivity.class));
                                    uploadImgToFirebase(imageUri);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.v(TAG, "signup" + e.toString());
                                }
                            });


                        }else{
                            Toast.makeText(NewUserActivity.this, "Error: " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) ;
            {
                imageUri = data.getData();
                iv_userImage.setImageURI(imageUri);

            }
        }
        if (resultCode == 0) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iv_userImage.setImageBitmap(imageBitmap);
        }
    }

    private void uploadImgToFirebase(Uri imageUri) {
        //upload imgage to firbasestorage
        StorageReference fileRef = storageReference.child("users/"+ mAuth.getUid()+"profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(NewUserActivity.this, "Image uploaded", Toast.LENGTH_SHORT);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewUserActivity.this, "Failed to upload image", Toast.LENGTH_SHORT);
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
        progressBar     = findViewById(R.id.progressBar);
    }


}

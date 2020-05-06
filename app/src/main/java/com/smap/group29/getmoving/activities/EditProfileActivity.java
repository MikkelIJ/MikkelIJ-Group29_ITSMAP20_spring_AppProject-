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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smap.group29.getmoving.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText edit_name, edit_email, edit_age, edit_city, edit_steps;
    ImageView iv_profilePicture;
    Button btn_update, btn_cancel, btn_newPicture;

    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    FirebaseUser fUser;
    StorageReference fbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        fUser = mAuth.getCurrentUser();
        fbRef = FirebaseStorage.getInstance().getReference();

        Intent data = getIntent();
        final String name = data.getStringExtra("name");
        String profileEmail = data.getStringExtra("email");
        String age = data.getStringExtra("age");
        String city = data.getStringExtra("city");
        String steps = data.getStringExtra("steps");

        Log.d(TAG, "onCreate: "  + name +" " +" " + age+" " + city +" "+ steps);

        initUi();

        StorageReference imgProfile = fbRef.child("users/"+ mAuth.getUid()+"profile.jpg");
        imgProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(iv_profilePicture);

            }

        });

        edit_name.setText(name);
        edit_email.setText(profileEmail);
        edit_age.setText(age);
        edit_city.setText(city);
        edit_steps.setText(steps);

        btn_newPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCamararollIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openCamararollIntent, 102);

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if any of the fields are empty
                if(edit_name.getText().toString().isEmpty() || edit_age.getText().toString().isEmpty() || edit_city.getText().toString().isEmpty() || edit_steps.getText().toString().isEmpty()){
                    Toast.makeText(EditProfileActivity.this, "One or more fields are empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String email = edit_email.getText().toString();
                fUser.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Updating the userdata from edittexts input
                        DocumentReference documentReference = mStore.collection("users").document(fUser.getUid());
                        Map<String, Object> editedUser = new HashMap<>();
                        editedUser.put("age", edit_age.getText().toString());
                        editedUser.put("city", edit_city.getText().toString());
                        editedUser.put("dailysteps", edit_steps.getText().toString());
                        editedUser.put("email",email);
                        editedUser.put("name", edit_name.getText().toString());

                        documentReference.update(editedUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), UserActivity.class));
                                finish();
                            }
                        });
                        Toast.makeText(EditProfileActivity.this, "User is updated",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) ;
            {
                Uri imageUri = data.getData();
                uploadImgToFirebase(imageUri);

            }
            /*
        }
        if (resultCode == 0) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iv_userImage.setImageBitmap(imageBitmap);
        }

             */
    }
    }

    private void uploadImgToFirebase(final Uri imageUri) {
        //upload imgage to firbasestorage
        final StorageReference fileRef = fbRef.child("users/"+ mAuth.getUid()+"profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(EditProfileActivity.this, "Image uploaded", Toast.LENGTH_SHORT);
                        Picasso.get().load(imageUri).into(iv_profilePicture);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT);
            }
        });
    }



    private void initUi() {
        edit_name = findViewById(R.id.edit_name);
        edit_email = findViewById(R.id.edit_email);
        edit_age = findViewById(R.id.edit_age);
        edit_city = findViewById(R.id.edit_city);
        edit_steps = findViewById(R.id.edit_dailygoal);
        btn_cancel = findViewById(R.id.btn_cancelEdit);
        btn_update = findViewById(R.id.btn_update);
        btn_newPicture = findViewById(R.id.btn_changePhoto);
        iv_profilePicture = findViewById(R.id.iv_newUserImage);
    }
}

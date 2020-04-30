package com.smap.group29.getmoving.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smap.group29.getmoving.Models.NewUser;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.Utils.FirebaseUtil;
import com.squareup.picasso.Picasso;

public class NewUserActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private static final int PICTURE_RESULT = 33;
    NewUser mNewUser;

    // variables for the edit text of NewUser
    EditText mEditTextName;
    EditText mEditTextAge;
    EditText mEditTextCity;
    EditText mEditTextDailySteps;
    ImageView mImageView;
    Button mButtonAddImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("NewUser");

        // referencing to each editText
        mEditTextName = findViewById(R.id.et_name);
        mEditTextAge = findViewById(R.id.et_age);
        mEditTextCity = findViewById(R.id.et_city);
        mEditTextDailySteps = findViewById(R.id.et_dailygoal);
        mImageView = findViewById(R.id.iv_createnewuser);

//        showImage(mNewUser.getImageUrl());

//        mButtonAddImage = findViewById(R.id.btn_addPhoto);

//        mButtonAddImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent  = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/jpeg");
//                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true );
//                startActivityForResult(intent.createChooser(intent, "Insert Image"), PICTURE_RESULT);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        return true;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
//            Uri imageUri = data.getData();
//            final StorageReference ref = FirebaseUtil.mStorageReference.child(imageUri.getLastPathSegment());
//            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                    String url = ref.getDownloadUrl().toString();
//                    //String url = taskSnapshot.getDownloadUrl().toString();
//                    String pictureName = taskSnapshot.getStorage().getPath();
//                    mNewUser.setImageUrl(url);
//                    mNewUser.setImageName(pictureName);
//                    Log.d("Url: ", url);
//                    Log.d("Name", pictureName);
//                    showImage(url);
//                }
//            });
//
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            // TODO: should change to save_menu
            case R.id.save_menu:
                saveNewUser();
                Toast.makeText(this, "New User Saved!", Toast.LENGTH_LONG).show();
                cleanAll(); // clearing the edit texts

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveNewUser() {
        String name = mEditTextName.getText().toString();
        String age = mEditTextAge.getText().toString();
        String city = mEditTextCity.getText().toString();
        String dailySteps = mEditTextDailySteps.getText().toString();

        // Constructing NewUser & PUSH to the database
        NewUser newUser = new NewUser (name, age, city, dailySteps, "", "");
        mDatabaseReference.push().setValue(newUser);

//        mDatabaseReference.child("users").setValue(newUser)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        // Write was successful!
//                        Log.d("Saving", "Saved to the Database!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Write failed
//                        Log.d("Not Saving", "Failed to save to the Database!");
//                    }
//                });
    }

    public void cleanAll() {
        mEditTextName.setText("");
        mEditTextAge.setText("");
        mEditTextCity.setText("");
        mEditTextDailySteps.setText("");
    }

    private void showImage(String url) {
        if (url != null && url.isEmpty() == false) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(mImageView);
        }
    }

//    private void deleteUser() {
//        if (mNewUser == null) {
//            Toast.makeText(this, "Save before deleting", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        mDatabaseReference.child(mNewUser.getId()).removeValue();
//        Log.d("image name", mNewUser.getImageName());
//        if(mNewUser.getImageName() != null && mNewUser.getImageName().isEmpty() == false) {
//            StorageReference picRef = FirebaseUtil.mStorageReference.child(mNewUser.getImageName());
//            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Log.d("Delete Image", "Image Successfully Deleted!");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.d("Delete Image", e.getMessage());
//                }
//            });
//        }
//
//    }

}

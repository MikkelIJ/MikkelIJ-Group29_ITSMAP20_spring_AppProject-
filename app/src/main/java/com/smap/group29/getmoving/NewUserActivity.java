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

    private TextView tv_createNewUser_header;
    private ImageView iv_userImage;
    private Button btn_addPhoto;
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
        btn_addPhoto    = findViewById(R.id.btn_addPhoto);
        et_name         = findViewById(R.id.et_name);
        et_age          = findViewById(R.id.et_age);
        et_city         = findViewById(R.id.et_city);
        et_dailySteps   = findViewById(R.id.et_dailygoal);
        btn_save        = findViewById(R.id.btn_save);
        btn_cancel      = findViewById(R.id.btn_cancel);
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
                readDatabase();
                if (et_name.getText().toString() != "" && et_age.getText().toString() != "" && et_city.getText().toString() != "" && et_dailySteps.getText().toString() != ""){
                    newUser = new User(et_name.getText().toString(),et_age.getText().toString(),et_city.getText().toString(),et_dailySteps.getText().toString());

                    String keyId = myDBRef.push().getKey();
                    myDBRef.child(keyId).setValue(newUser);
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

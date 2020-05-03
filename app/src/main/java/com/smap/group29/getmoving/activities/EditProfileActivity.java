package com.smap.group29.getmoving.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.smap.group29.getmoving.R;

public class EditProfileActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText edit_name, edit_email, edit_age, edit_city, edit_steps;
    ImageView iv_profilePicture;
    Button btn_update, btn_cancel;

    FirebaseAuth mAuth;
    FirebaseStorage mStore;
    FirebaseUser fUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseStorage.getInstance();
        fUser = mAuth.getCurrentUser();

        Intent data = getIntent();
        final String name = data.getStringExtra("name");
        String email = data.getStringExtra("email");
        String age = data.getStringExtra("age");
        String city = data.getStringExtra("city");
        String steps = data.getStringExtra("steps");

        Log.d(TAG, "onCreate: "  + name +" " +" " + age+" " + city +" "+ steps);

        initUi();

        edit_name.setText(name);
        edit_email.setText(email);
        edit_age.setText(age);
        edit_city.setText(city);
        edit_steps.setText(steps);


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if any of the fields are empty
                if(edit_name.getText().toString().isEmpty() || edit_age.getText().toString().isEmpty() || edit_city.getText().toString().isEmpty() || edit_steps.getText().toString().isEmpty()){
                    Toast.makeText(EditProfileActivity.this, "One or more fields are empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //     fUser.updateEmail(name).addOnSuccessListener()



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
    }
}

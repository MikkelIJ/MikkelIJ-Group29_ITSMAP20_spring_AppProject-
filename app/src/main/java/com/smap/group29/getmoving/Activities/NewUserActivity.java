package com.smap.group29.getmoving.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smap.group29.getmoving.Models.NewUser;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.Utils.FirebaseUtil;

public class NewUserActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    // variables for the edit text of NewUser
    EditText mEditTextName;
    EditText mEditTextAge;
    EditText mEditTextCity;
    EditText mEditTextDailySteps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("NewUser");

        // referencing to each editText
        mEditTextName = findViewById(R.id.et_name);
        mEditTextAge = findViewById(R.id.et_age);
        mEditTextCity = findViewById(R.id.et_city);
        mEditTextDailySteps = findViewById(R.id.et_dailygoal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            // TODO: should change to save_menu
            case R.id.btn_save:
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
        NewUser newUser = new NewUser (name, age, city, dailySteps, "");
        mDatabaseReference.push().setValue(newUser);
    }

    public void cleanAll() {
        mEditTextName.setText("");
        mEditTextAge.setText("");
        mEditTextCity.setText("");
        mEditTextDailySteps.setText("");
    }

}

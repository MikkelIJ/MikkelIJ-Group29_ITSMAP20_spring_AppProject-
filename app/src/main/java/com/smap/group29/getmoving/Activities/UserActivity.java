package com.smap.group29.getmoving.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.Utils.FirebaseUtil;


public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       FirebaseUtil.openFirebaseReference("TheNewRealDeal",this);
    }


    @Override
    protected void onPause(){
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume(){
        super.onResume();
        FirebaseUtil.attachListener();
    }
}

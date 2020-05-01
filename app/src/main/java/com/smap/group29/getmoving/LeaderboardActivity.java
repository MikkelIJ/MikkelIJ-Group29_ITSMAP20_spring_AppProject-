package com.smap.group29.getmoving;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.smap.group29.getmoving.adaptor.LeaderboardAdaptor;
import com.smap.group29.getmoving.model.User;
import com.smap.group29.getmoving.utils.DummyData;

import java.util.ArrayList;
import java.util.List;

// Firebase recyclerview inspired by https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md#using-the-firebaserecycleradapter
public class LeaderboardActivity extends AppCompatActivity {

    private Button btn_back;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List <User> mUserList = new ArrayList<>();
    private List <User> mUserList2 = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        mUserList.addAll(DummyData.getUsers());

        initUI();
        initRecyclerView();
        setUI();


    }






    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new LeaderboardAdaptor(mUserList, this);
        recyclerView.setAdapter(mAdapter);
    }

    private void initUI() {
        btn_back = findViewById(R.id.btn_leaderboardBack);
        recyclerView = findViewById(R.id.rv_leaderboard);
    }

    private void setUI() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

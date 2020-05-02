package com.smap.group29.getmoving;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smap.group29.getmoving.Utils.LeaderBoardAdapter;

public class LeaderboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        RecyclerView recyclerViewNewUsers = findViewById(R.id.recyclerView);
        final LeaderBoardAdapter adapter = new LeaderBoardAdapter();
        recyclerViewNewUsers.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewNewUsers.setLayoutManager(linearLayoutManager);
    }

}
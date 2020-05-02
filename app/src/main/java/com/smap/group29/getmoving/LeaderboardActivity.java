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



//public class LeaderboardActivity extends AppCompatActivity {
//
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private CollectionReference userRef = db.collection("KspUsers");
//
//    private LeaderboardAdaptor mAdaptor;
//    private Button btn_back;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_leaderboard);
//
//        initUI();
//        setupRecyclerView();
//        setUI();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mAdaptor.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mAdaptor.stopListening();
//    }
//
//    private void setupRecyclerView() {
//        RecyclerView mRecyclerView = findViewById(R.id.rv_leaderboard);
//        Query query = userRef.orderBy("age",Query.Direction.DESCENDING);
//        FirestoreRecyclerOptions<User> mOptions = new FirestoreRecyclerOptions.Builder<User>().setQuery(query,User.class).build();
//
//        mAdaptor = new LeaderboardAdaptor(mOptions);
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setAdapter(mAdaptor);
//    }
//
//
//
//    private void initUI() {
//        btn_back = findViewById(R.id.btn_leaderboardBack);
//    }
//
//    private void setUI() {
//        btn_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }
//}

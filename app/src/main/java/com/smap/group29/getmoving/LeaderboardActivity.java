package com.smap.group29.getmoving;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.smap.group29.getmoving.adaptor.LeaderboardAdaptor;
import com.smap.group29.getmoving.model.NewUser;


// Firebase recyclerview inspired by https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md#using-the-firebaserecycleradapter
// and https://codinginflow.com/tutorials/android/firebaseui-firestorerecycleradapter/part-4-new-note-activity
public class LeaderboardActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("KspUsers");

    private LeaderboardAdaptor mAdaptor;
    private Button btn_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        initUI();
        setupRecyclerView();
        setUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdaptor.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdaptor.stopListening();
    }

    private void setupRecyclerView() {
        RecyclerView mRecyclerView = findViewById(R.id.rv_leaderboard);
        Query query = userRef.orderBy("email",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions.Builder<NewUser> userBuilder = new FirestoreRecyclerOptions.Builder<>();
        userBuilder.setQuery(query, NewUser.class);
        FirestoreRecyclerOptions<NewUser> mOptions = userBuilder.build();

        mAdaptor = new LeaderboardAdaptor(mOptions);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdaptor);
    }



    private void initUI() {
        btn_back = findViewById(R.id.btn_leaderboardBack);
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

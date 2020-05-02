package com.smap.group29.getmoving.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smap.group29.getmoving.Models.NewUser;
import com.smap.group29.getmoving.R;

import java.util.ArrayList;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardViewHolder>{

    ArrayList<NewUser> mNewUserList;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    public LeaderBoardAdapter() {
//        FirebaseUtil.openFirebaseReference("NewUser");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        mNewUserList = FirebaseUtil.mNewUsers; // getting list of new user from Util class and view it here.
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NewUser newUser = dataSnapshot.getValue(NewUser.class);
                Log.d("NewUser", newUser.getName());
                newUser.setId(dataSnapshot.getKey());
                mNewUserList.add(newUser);
                notifyItemInserted(mNewUserList.size()-1); // passing the array position - notifying observers of inserted item. UI get updated.
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);

    }

    @NonNull @Override
    public LeaderBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View ItemView = LayoutInflater.from(context)
                .inflate(R.layout.user_item, parent, false);
        return new LeaderBoardViewHolder(ItemView); // passing the itemView to the viewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderBoardViewHolder holder, int position) {
        NewUser newUser = mNewUserList.get(position); // getting position and binding it to the new user
        holder.bind(newUser);
    }

    @Override
    public int getItemCount() {
        return mNewUserList.size(); // returning the size of the arrayList.
    }

    public class LeaderBoardViewHolder extends RecyclerView.ViewHolder{
        // variables
        TextView mTextViewUserName, mTextViewUserScore;

        public LeaderBoardViewHolder(@NonNull View itemView) {
            super(itemView);
            // using varable to reference
            mTextViewUserName = itemView.findViewById(R.id.tv_userName_desc);
            mTextViewUserScore = itemView.findViewById(R.id.tv_userScore);
        }

        public void bind(NewUser newUser){
            mTextViewUserName.setText(newUser.getName());
            mTextViewUserScore.setText(newUser.getDailySteps());
        }
    }



}


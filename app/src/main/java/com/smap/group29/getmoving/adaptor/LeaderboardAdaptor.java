package com.smap.group29.getmoving.adaptor;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.model.NewUser;


public class LeaderboardAdaptor extends FirestoreRecyclerAdapter<NewUser, LeaderboardAdaptor.UserHolder> {


    public LeaderboardAdaptor(@NonNull FirestoreRecyclerOptions<NewUser> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull NewUser model) {
        holder.tv_userRank.setText(String.valueOf(position+1)); // + 1 to remove 0
        holder.tv_userName.setText(model.getName());
        holder.tv_userSteps.setText(model.getDailysteps());
        Log.v("user",String.valueOf(position+1) + " " + model.getName() + " " + model.getDailysteps());

    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        return new UserHolder(v);

    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        Log.v("user_error",e.getMessage());
    }

    class UserHolder extends RecyclerView.ViewHolder {

        TextView tv_userRank;
        TextView tv_userName;
        TextView tv_userSteps;
        ImageView iv_userImage;

        public UserHolder( View itemView) {
            super(itemView);

            tv_userRank = itemView.findViewById(R.id.tv_itemRank);
            tv_userName = itemView.findViewById(R.id.tv_itemName);
            tv_userSteps = itemView.findViewById(R.id.tv_itemSteps);
            iv_userImage = itemView.findViewById(R.id.iv_itemImage);
        }
    }
}

package com.smap.group29.getmoving.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.model.User;

import java.util.List;

import static com.smap.group29.getmoving.R.drawable.noimage;

public class LeaderboardAdaptor extends RecyclerView.Adapter<LeaderboardAdaptor.MyViewHolder>{

    private List<User> mUserList;
    private Context mContext;

    public LeaderboardAdaptor(List<User> mUserList, Context mContext) {
        this.mUserList = mUserList;
        this.mContext = mContext;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_userImage;
        TextView tv_user_name;
        TextView tv_user_steps;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_userImage = itemView.findViewById(R.id.iv_leaderBoardImage);
            tv_user_name = itemView.findViewById(R.id.tv_leaderboardName);
            tv_user_steps = itemView.findViewById(R.id.tv_leaderboardSteps);
        }


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User currentUser = mUserList.get(position);
        holder.tv_user_name.setText("Bobby");
        holder.tv_user_steps.setText("100000");
    }

    @Override
    public int getItemCount() {
        return mUserList != null ? mUserList.size() : 0;
    }
}

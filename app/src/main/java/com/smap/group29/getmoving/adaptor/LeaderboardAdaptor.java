package com.smap.group29.getmoving.adaptor;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.model.NewUser;
import com.squareup.picasso.Picasso;

// inspired by code in flow https://www.youtube.com/watch?v=3WR4QAiVuCw&list=RDCMUC_Fh8kvtkVPkeihBs42jGcA&start_radio=1&t=507

public class LeaderboardAdaptor extends FirestoreRecyclerAdapter<NewUser, LeaderboardAdaptor.UserHolder>  {

    StorageReference fbRef = FirebaseStorage.getInstance().getReference();
    private OnItemClickListener mListener;


    public LeaderboardAdaptor(@NonNull FirestoreRecyclerOptions<NewUser> options) {
        super(options);
    }

    class UserHolder extends RecyclerView.ViewHolder{

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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mListener != null){
                        mListener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }





    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull final UserHolder holder, int position, @NonNull NewUser model) {
        //holder.tv_userRank.setText(setPosition(position+1)); // + 1 to remove 0  // TODO make position update when datachange
        holder.tv_userName.setText(model.getName());
        holder.tv_userSteps.setText(String.valueOf(model.getDailysteps()));



        StorageReference imgProfile = fbRef.child("users/"+ model.getuID()+"profile.jpg");
        imgProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.iv_userImage);
            }
        });

        //Log.v("user",String.valueOf(position+1) + " " + model.getName() + " " + model.getDailysteps());

    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

    }


    private String setPosition(int position){
        if (position == 1){
            return "1\ud83c\udfc6";
        }else
            return String.valueOf(position);
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


}

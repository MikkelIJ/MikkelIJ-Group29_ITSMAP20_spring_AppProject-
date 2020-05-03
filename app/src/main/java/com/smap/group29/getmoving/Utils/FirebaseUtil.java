package com.smap.group29.getmoving.Utils;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smap.group29.getmoving.Models.NewUser;
import com.smap.group29.getmoving.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    public static FirebaseUtil firebaseUtil;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageReference;

    public static ArrayList<NewUser> mNewUsers;

    private static Activity caller;

    public FirebaseUtil(){}

    public static void openFirebaseReference(String ref, final Activity callerActivity){
        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mNewUsers = new ArrayList<NewUser>();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;

            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    // check if the user is logged in else don't need to
                    if (firebaseAuth.getInstance().getCurrentUser() == null ){ FirebaseUtil.signIn(); }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                }
            };
            connectToStorage();
        }
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }



    public static void signIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                1);
    }

    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    public static void connectToStorage(){
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("users_images");
    }
}

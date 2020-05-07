package com.smap.group29.getmoving.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smap.group29.getmoving.activities.LoginActivity;
import com.smap.group29.getmoving.activities.UserActivity;

import java.util.Arrays;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class FirebaseUtil {

    private static FirebaseUtil firebaseUtil;
    private static FirebaseFirestore mStore;
    private static CollectionReference collectionReference;
    private static DocumentReference documentReference;
    private static StorageReference storageReference;
    private static Activity caller;
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;



    private FirebaseUtil(){}

    public static void openFirebaseReference(String userID, final Activity callerActivity){
        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            mAuth = FirebaseAuth.getInstance();
            mStore = FirebaseFirestore.getInstance();
            collectionReference = mStore.collection(GlobalConstants.FIREBASE_USER_COLLECTION);
            storageReference = FirebaseStorage.getInstance().getReference();
            caller = callerActivity;

            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    // check if the user is logged in else don't need to
                    if (firebaseAuth.getInstance().getCurrentUser() == null ){ FirebaseUtil.signIn("test","test"); }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                }
            };
        }
        documentReference = mStore.collection(GlobalConstants.FIREBASE_USER_COLLECTION).document(userID);
    }

    private static void signIn(String email, String password){


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(LoginActivity.this, "Logged in succesfully", Toast.LENGTH_SHORT).show();
                    caller.startActivity(new Intent(caller, LoginActivity.class));

                }
            }
        });
    }

    public static void attachListener() {
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    public static void detachListener(){
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
}

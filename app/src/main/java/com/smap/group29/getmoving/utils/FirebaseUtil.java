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
import com.google.firebase.firestore.DocumentSnapshot;
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



    public FirebaseUtil(){}





}

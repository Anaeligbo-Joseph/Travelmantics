package com.joseph.alc4.travelmantics.util;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.joseph.alc4.travelmantics.TravelListActivity;
import com.joseph.alc4.travelmantics.model.TravelDeal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    private static FirebaseAuth mfirebaseAuth;
    private static FirebaseAuth.AuthStateListener mAuthStateListener;
    private static FirebaseUtil firebaseUtil;
    public static ArrayList<TravelDeal> travelDeals;
    private static TravelListActivity caller;
    private static final int RC_SIGN_IN = 123;
    public static boolean isAdmin;
    private static FirebaseStorage firebaseStorage;
    public static StorageReference storageReference;

    private FirebaseUtil(){
    }

    public static void openFbReference(String ref, final TravelListActivity callerActivity){
        if (firebaseUtil==null){
            firebaseUtil= new FirebaseUtil();
            firebaseDatabase=firebaseDatabase.getInstance();
            mfirebaseAuth=FirebaseAuth.getInstance();
            caller=callerActivity;
            mAuthStateListener=new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                   if(firebaseAuth.getCurrentUser()==null){
                       FirebaseUtil.signIn();
                }else{
                    String userID = firebaseAuth.getUid();
                    checkAdmin(userID);
                }
                Toast.makeText(callerActivity.getBaseContext(),"Welcome Back",Toast.LENGTH_SHORT).show();
            }
            };
        }
        travelDeals=new ArrayList<TravelDeal>();
        databaseReference=firebaseDatabase.getReference().child(ref);
        connectStorage();
    }

    private static void checkAdmin(String uid) {
        FirebaseUtil.isAdmin = false;
        DatabaseReference ref = firebaseDatabase.getReference().child("administrators").
                child(uid);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();
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
        ref.addChildEventListener(listener);
    }

    private static void signIn(){
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
                RC_SIGN_IN);
    }

    public static void attachListener(){
        mfirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    public static void detachListener(){
        mfirebaseAuth.removeAuthStateListener(mAuthStateListener);

    }

    private static void connectStorage() {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("deals_pictures");
    }
}

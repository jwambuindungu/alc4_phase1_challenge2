package com.example.android.travelmantics;

import android.app.Activity;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyDatabaseUtils {

    public static FirebaseDatabase myDatabase;
    public static DatabaseReference myDatabaseReference;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageReference;
    public static ArrayList<travelDeal> myDeals;
    private static ListActivity myActivity;
    public static boolean isAdmin;
    private static MyDatabaseUtils databaseUtils;

    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "MyDatabaseUtils";

    private MyDatabaseUtils() {
    }

    public static void OpenDBReference(String passed_ref, final ListActivity CallerActivity)
    {
        if (databaseUtils == null){
            myActivity = CallerActivity;
            databaseUtils= new MyDatabaseUtils();

            myDatabase= FirebaseDatabase.getInstance();
            mFirebaseAuth= FirebaseAuth.getInstance();
            mFirebaseAuthListener = new FirebaseAuth.AuthStateListener(){
                @Override
                public void onAuthStateChanged( FirebaseAuth firebaseAuth) {

                    if (firebaseAuth.getCurrentUser() == null) {
                        MyDatabaseUtils.Sign_in();
                    }
                    else
                    {
                       String Userid= firebaseAuth.getUid();
                        Log.d(TAG, "onAuthStateChanged: " + Userid);
                       CheckIfAdmin(Userid);

                    }
                    Toast.makeText(myActivity.getBaseContext(), "Welcome Back", Toast.LENGTH_SHORT).show();

                }
            };
            connectStorage();
        }
        myDeals= new ArrayList<travelDeal>();
        myDatabaseReference=myDatabase.getReference().child(passed_ref);
    }

    private static void CheckIfAdmin(String LoggedInUser) {
        MyDatabaseUtils.isAdmin=false;
        DatabaseReference AdminRef=myDatabase.getReference().child("administrator").child(LoggedInUser);

        ChildEventListener myChildlistener= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MyDatabaseUtils.isAdmin=true;
                Log.d(TAG, "Admin: This user is an Administrator");
                myActivity.showMenu();
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
        AdminRef.addChildEventListener(myChildlistener);
    }

    public static void AttachListener(){

        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
    }

    public static void DetachListener () {

        mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);
    }

    private static void Sign_in() {
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());
            // Create and launch sign-in intent
            myActivity.startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);
    }
    public static void connectStorage() {
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference().child("deals_pictures");
    }
}

package com.example.android.travelmantics;

import android.content.Intent;
import android.os.Bundle;

import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    ChildEventListener mChildEventListener;
    private static final String TAG = "ListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //MyDatabaseUtils.OpenDBReference("traveldeals");
//        mFirebaseDatabase = MyDatabaseUtils.myDatabase;
//        mDatabaseReference = MyDatabaseUtils.myDatabaseReference;

        if (BuildConfig.DEBUG) {
            StrictMode.VmPolicy vmPolicy=new StrictMode.VmPolicy.Builder()
                    .detectActivityLeaks()
                    .penaltyLog()
                    .build();
            StrictMode.setVmPolicy(vmPolicy);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mymenu = getMenuInflater();
        mymenu.inflate(R.menu.list_menu_activity, menu);
        MenuItem InsertMenu= menu.findItem(R.id.insert_menu);
        if (MyDatabaseUtils.isAdmin == true)
        {
            menu.findItem(R.id.insert_menu).setVisible(true);
            //InsertMenu.setVisible(true);
        }
        else
        {
            menu.findItem(R.id.insert_menu).setVisible(false);
            //InsertMenu.setVisible(false);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_menu:
                Intent intent = new Intent(this, DealActivity.class);
                startActivity(intent);
                return true;
            case R.id.log_out:
                signout();
                return true;
            default:
                return true;

        }
    }

    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete( Task<Void> task) {
                        Log.d(TAG, "onComplete: Successfully Log out");
                        MyDatabaseUtils.AttachListener();
                    }
                });
        MyDatabaseUtils.DetachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyDatabaseUtils.OpenDBReference("traveldeals", this);
        RecyclerView rvDeals = (RecyclerView) findViewById(R.id.rvDeals);
        final travelDealAdapter adapter = new travelDealAdapter();
        rvDeals.setAdapter(adapter);
        //LinearLayoutManager dealsLayout= new LinearLayoutManager(this);
        LinearLayoutManager dealsLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvDeals.setLayoutManager(dealsLayoutManager);
        MyDatabaseUtils.AttachListener();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MyDatabaseUtils.DetachListener();

    }
    public void showMenu() {
        invalidateOptionsMenu();
    }
}

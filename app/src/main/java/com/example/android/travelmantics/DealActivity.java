package com.example.android.travelmantics;


import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {
    private static final String TAG = "DealActivity";
    private final int IMAGE_RESULT = 34;
    private FirebaseDatabase myDatabase;
    private DatabaseReference myDatabaseReference;
    EditText txtTitle;
    EditText txtDescription;
    EditText txtPrice;
    travelDeal deal;
    ImageView uploadImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);

       // MyDatabaseUtils.OpenDBReference("traveldeals",this);
        myDatabase=MyDatabaseUtils.myDatabase;
        myDatabaseReference =MyDatabaseUtils.myDatabaseReference;
//        myDatabase= FirebaseDatabase.getInstance();
//        myDatabaseReference=myDatabase.getReference().child("traveldeals");

        if (BuildConfig.DEBUG) {
            StrictMode.VmPolicy vmPolicy=new StrictMode.VmPolicy.Builder()
                    .detectActivityLeaks()
                    .penaltyLog()
                    .build();
            StrictMode.setVmPolicy(vmPolicy);
        }



        txtTitle= (EditText) findViewById(R.id.txtTitle);
        txtPrice= (EditText) findViewById(R.id.txtPrice);
        txtDescription= (EditText) findViewById(R.id.txtDescription);
        uploadImage= (ImageView) findViewById(R.id.myImage);
        Intent intent= getIntent();
        travelDeal receivedDeal=(travelDeal)intent.getSerializableExtra("Deal");


        if (receivedDeal==null) {
            receivedDeal = new travelDeal();
            Log.d(TAG, "onCreate: I'm empty for now ");
        }
        this.deal = receivedDeal;
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl());
        Button UploadButton =  findViewById(R.id.upload_image);
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent,
                        "Upload Picture"), IMAGE_RESULT);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            StorageReference ref = MyDatabaseUtils.mStorageReference.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    /*
                    String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                    String pictureName = taskSnapshot.getStorage().getPath();
                    deal.setImageUrl(url);
                    deal.setImageName(pictureName);
                    Log.d("Url: ", url);
                    Log.d("Name", pictureName);
                    showImage(url);*/

                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete( Task<Uri> task) {
                            String url = task.getResult().toString();
                            deal.setImageUrl(url);
                            deal.setImageName(taskSnapshot.getStorage().getPath());
                            showImage(url);
                        }
                    });



                }
            });

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mymenu= getMenuInflater();
        mymenu.inflate(R.menu.save_menu,menu);
        if (MyDatabaseUtils.isAdmin == true)
        {
            Log.d(TAG, "onCreateOptionsMenu: I'm administrator");
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
        }
        else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.save_menu:
                saveDeals();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_SHORT).show();
                clean();
                backToList();

                return true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_SHORT).show();
                backToList();
                return true;
                default:
                    return super.onOptionsItemSelected(item); 

        }
       
    }

    private void clean() {
       txtTitle.setText("");
       txtDescription.setText("");
       txtPrice.setText("");
       txtTitle.requestFocus();
    }


    private void saveDeals() {
        deal.setTitle(txtTitle.getText().toString());
        deal.setDescription(txtDescription.getText().toString());
        deal.setPrice(txtPrice.getText().toString());
        if(deal.getId()==null) {
            myDatabaseReference.push().setValue(deal);
        }
        else {
            myDatabaseReference.child(deal.getId()).setValue(deal);
        }
    }
    private void deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        myDatabaseReference.child(deal.getId()).removeValue();

    }
    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void showImage(String url) {
        if (url != null && url.isEmpty() == false) {
            Log.d(TAG, "showImage: Yes i'm here " );
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Log.d(TAG, "showImage: My width " + width );
//            Picasso.with(this)
//                    .load(url)
//                    .resize(width, width*2/3)
//                    .centerCrop()
//                    .into(uploadImage);

            Picasso.get()
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(uploadImage);


//            Glide.with(this)
//                    .load(url)
//                    .into(uploadImage);
        }
    }
    private void enableEditTexts(boolean isEnabled) {
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }



}

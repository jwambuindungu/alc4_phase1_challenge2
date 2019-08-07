package com.example.android.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class travelDealAdapter extends  RecyclerView.Adapter<travelDealAdapter.DealViewholder> {
    ArrayList<travelDeal> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    ChildEventListener mChildEventListener;
    private ImageView mImagedeal;
    private static final String TAG = "travelDealAdapter";
    public travelDealAdapter() {
        mFirebaseDatabase=MyDatabaseUtils.myDatabase;
        mDatabaseReference =MyDatabaseUtils.myDatabaseReference;
        deals=MyDatabaseUtils.myDeals;
        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded( DataSnapshot dataSnapshot,  String s) {

                travelDeal myDataShotDeal= dataSnapshot.getValue(travelDeal.class);
                myDataShotDeal.setId(dataSnapshot.getKey());
                Log.d(TAG, "onChildAdded: "+ myDataShotDeal.getTitle() );
                deals.add(myDataShotDeal);
                notifyItemInserted(deals.size()-1);

            }

            @Override
            public void onChildChanged( DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved( DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved( DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);



    }


    @Override
    public DealViewholder onCreateViewHolder( ViewGroup parent, int viewType) {
        Context context =parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.tv_row,parent,false);
        return new DealViewholder(itemView);
    }

    @Override
    public void onBindViewHolder( DealViewholder holder, int position) {
        travelDeal deal =deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class DealViewholder  extends RecyclerView.ViewHolder implements View.OnClickListener

    {
        TextView viewTitles;
        TextView viewDescription;
        TextView viewPrice;
        public DealViewholder(@NonNull View itemView) {
            super(itemView);
            viewTitles= (TextView) itemView.findViewById(R.id.tvTitle);
            viewDescription= (TextView) itemView.findViewById(R.id.tvDescription);
            viewPrice= (TextView) itemView.findViewById(R.id.tvPrice);
            mImagedeal = (ImageView) itemView.findViewById(R.id.imageDeal);
            itemView.setOnClickListener(this);
        }
        public void bind (travelDeal deal){

            viewTitles.setText(deal.getTitle());
            viewDescription.setText(deal.getDescription());
            viewPrice.setText(deal.getPrice());
            showImage(deal.getImageUrl());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));
            travelDeal  selectedDeal=deals.get(position);
            Log.d(TAG, "onClick:  The Price is " + selectedDeal.getPrice());
            Intent intent = new Intent(view.getContext(), DealActivity.class);
            intent.putExtra("Deal", selectedDeal);
            view.getContext().startActivity(intent);

        }
        private void showImage(String url) {
            if (url != null && url.isEmpty()==false) {
//                Picasso.with(mImagedeal.getContext())
//                        .load(url)
//                        .resize(160, 160)
//                        .centerCrop()
//                        .into(mImagedeal);
                Picasso.get()
                        .load(url)
                        .resize(160, 160)
                        .centerCrop()
                        .into(mImagedeal);




//                Glide.with(mImagedeal.getContext())
//                        .load(url)
//                        .into(mImagedeal);
            }
        }
    }
}

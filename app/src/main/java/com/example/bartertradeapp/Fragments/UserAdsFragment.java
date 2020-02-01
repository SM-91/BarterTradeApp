package com.example.bartertradeapp.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.MyAdsDetailsActivity;
import com.example.bartertradeapp.R;
import com.example.bartertradeapp.adapters.CustomListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserAdsFragment extends BaseFragment implements CustomListAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private CustomListAdapter adapter;
    private String ad_id;

    List<UserUploadProductModel> userUploadProductModels;

    DatabaseReference viewDatabaseReference;


    public UserAdsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_ads, container, false);

        uploadAuth = FirebaseAuth.getInstance();
        userUploadProductModels = new ArrayList<>();

        // set up the RecyclerView
        recyclerView = view.findViewById(R.id.userAdsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // initializing adapter
        adapter = new CustomListAdapter(getContext(), userUploadProductModels);
        adapter.setClickListener(this);

        viewDatabaseReference = FirebaseDatabase.getInstance().getReference("UserUploads")
                .child(uploadAuth.getCurrentUser().getUid());
        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userUploadProductModels.clear();

                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    UserUploadProductModel users = usersSnapshot.getValue(UserUploadProductModel.class);
                    userUploadProductModels.add(users);
                }
                // setting adapter to recycler View
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

   /* @Override
    public void onItemClick(View view, int position) {


    }*/

    @Override
    public void onItemClick(View view, int position) {
        intent = new Intent(getContext(), MyAdsDetailsActivity.class);
        userUploadProductModel = adapter.getItem(position);
        ad_id = userUploadProductModel.getAdId();
        Date myCurrentDateTime = userUploadProductModel.getCurrentDateTime();
        String myCurrentDateTimeString = userUploadProductModel.getCurrentDateTimeString();
        productName = userUploadProductModel.getProductName();
        productDescription = userUploadProductModel.getProductDescription();
        productPossibleExchangeWith = userUploadProductModel.getPossibleExchangeWith();
        productEstimatedPrice = userUploadProductModel.getProductEstimatedMarketValue();
        productType = userUploadProductModel.getProductType();
        productCategory = userUploadProductModel.getProductCategoryList();
        productCondition = userUploadProductModel.getProductCondition();
        productSingleImage = userUploadProductModel.getmImageUri();
        String serviceName = userUploadProductModel.getServiceName();
        String serviceCategory = userUploadProductModel.getServiceCategory();
        String serviceDescription = userUploadProductModel.getServiceDescription();
        String serviceEstimatedMarketValue = userUploadProductModel.getServiceEstimatedMarketValue();
        String servicePossibleExchangeWith = userUploadProductModel.getServicePossibleExchangeWith();
        String tag = userUploadProductModel.getTag();
        UserModel postedBy = userUploadProductModel.getPostedBy();
        String serviceImageUri = userUploadProductModel.getServiceImageUri();
        ArrayList<String> productMultipleImages = userUploadProductModel.getmArrList();

        intent.putExtra("ad_id", ad_id);
        //intent.putExtra("Key", myCurrentDateTime);
        intent.putExtra("myCurrentDateTimeString", myCurrentDateTimeString);
        intent.putExtra("name", productName);
        intent.putExtra("description", productDescription);
        intent.putExtra("possibelExchange", productPossibleExchangeWith);
        intent.putExtra("estimatedPrice", productEstimatedPrice);
        intent.putExtra("type", productType);
        intent.putExtra("category", productCategory);
        intent.putExtra("condition", productCondition);
        intent.putExtra("serviceName", serviceName);
        intent.putExtra("serviceCategory", serviceCategory);
        intent.putExtra("serviceDescription", serviceDescription);
        intent.putExtra("serviceEstimatedMarketValue", serviceEstimatedMarketValue);
        intent.putExtra("servicePossibleExchangeWith", servicePossibleExchangeWith);
        intent.putExtra("serviceImageUri", serviceImageUri);
        intent.putExtra("tag", tag);
        intent.putExtra("postedBy", postedBy);
        intent.putExtra("multipleImagesList", productMultipleImages);
        intent.putExtra("singleImage", productSingleImage);
        startActivity(intent);
    }

    @Override
    public void onLatestItemClick(View view, int position) {

    }
}

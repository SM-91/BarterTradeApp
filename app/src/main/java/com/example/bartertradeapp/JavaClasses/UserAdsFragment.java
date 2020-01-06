package com.example.bartertradeapp.JavaClasses;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.DetailedActivity;
import com.example.bartertradeapp.R;
import com.example.bartertradeapp.adapters.custom_list_adapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserAdsFragment extends BaseFragment implements custom_list_adapter.ItemClickListener {

    private RecyclerView recyclerView;
    private custom_list_adapter adapter;

    List<UserUploadProductModel> userUploadProductModels;

    public UserAdsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_user_ads, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("UserUploadProducts");
        userUploadProductModels = new ArrayList<>();
        // hardcode adding items to list for testing reasons
        //userlist.add(new UserUploadProductModel("Shayan","pagaal hy shayan","good","asdasdsadasdadasd"));

        // set up the RecyclerView
        recyclerView = view.findViewById(R.id.userAdsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // initializing adapter
        adapter = new custom_list_adapter (getContext(), userUploadProductModels);
        adapter.setClickListener(this);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userUploadProductModels.clear();
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()){
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

    @Override
    public void onItemClick(View view, int position) {

        /*intent = new Intent(getContext(), DetailedActivity.class);
        userUploadProductModel = adapter.getItem(position);
        pname = userUploadProductModel.getProductName();
        pdesc = userUploadProductModel.getProductDescription();
        pexch = userUploadProductModel.getPossibleExchangeWith();
        pworth = userUploadProductModel.getProductEstimatedMarketValue();
        pimg = userUploadProductModel.getmImageUri();

        intent.putExtra("name", pname);
        intent.putExtra("desc", pdesc);
        intent.putExtra("exch", pexch);
        intent.putExtra("worth", pworth);
        intent.putExtra("image", pimg);
        startActivity(intent);*/

        userUploadProductModel = adapter.getItem(position);
        String id = userUploadProductModel.getAdId();
        pname = userUploadProductModel.getProductName();
        pdesc = userUploadProductModel.getProductDescription();
        pexch = userUploadProductModel.getPossibleExchangeWith();
        pest = userUploadProductModel.getProductEstimatedMarketValue();
        ptype = userUploadProductModel.getProductType();
        pcategory = userUploadProductModel.getProductCategoryList();
        pcondition = userUploadProductModel.getProductCondition();
        pimg = userUploadProductModel.getmImageUri();
        ArrayList<String> multipleImagesList = userUploadProductModel.getmArrList();

        UpdateProductFragment updateProductFragment = new UpdateProductFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString("AD_ID",id);
        bundle.putString("name", pname);
        bundle.putString("desc", pdesc);
        bundle.putString("exch", pexch);
        bundle.putString("worth", pest);
        bundle.putString("image", pimg);
        bundle.putString("type", ptype);
        bundle.putString("category", pcategory);
        bundle.putString("condition", pcondition);
        bundle.putStringArrayList("multipleImagesList", multipleImagesList);
        updateProductFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.fragment_container,updateProductFragment);
        fragmentTransaction.commit();

    }
}

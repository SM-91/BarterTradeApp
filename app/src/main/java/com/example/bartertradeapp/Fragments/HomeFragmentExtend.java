package com.example.bartertradeapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.DetailedActivity;
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
import java.util.regex.Pattern;

public class HomeFragmentExtend extends BaseFragment implements CustomListAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private CustomListAdapter adapter;
    LinearLayoutManager layoutManager;
    SearchView searchbar;
    FirebaseAuth uploadAuth;
    List<UserUploadProductModel> userlist;
    TextView text_search;


    private String ad_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_fragment_extend, container, false);


        uploadAuth = FirebaseAuth.getInstance();
        userlist = new ArrayList<>();

        // set up the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView_allproducts);
        searchbar = view.findViewById(R.id.search_bar);
        text_search = view.findViewById(R.id.textview_search_text);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        // initializing adapter
        adapter = new CustomListAdapter(getContext(), userlist);
        adapter.setClickListener(this);

        text_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchbar.onActionViewExpanded();
            }
        });

        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });

        DatabaseReference viewDatabaseReference;
        viewDatabaseReference = FirebaseDatabase.getInstance().getReference("ProductsAndServices");
        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userlist.clear();
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    UserUploadProductModel users = usersSnapshot.getValue(UserUploadProductModel.class);
                    userlist.add(users);
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

        intent = new Intent(getContext(), DetailedActivity.class);
        userUploadProductModel = adapter.getItem(position);
        ad_id = userUploadProductModel.getAdId();
        Date myCurrentDateTime = userUploadProductModel.getCurrentDateTime();
        String myCurrentDateTimeString = userUploadProductModel.getCurrentDateTimeString();
        pname = userUploadProductModel.getProductName();
        pdesc = userUploadProductModel.getProductDescription();
        pexch = userUploadProductModel.getPossibleExchangeWith();
        pest = userUploadProductModel.getProductEstimatedMarketValue();
        ptype = userUploadProductModel.getProductType();
        pcategory = userUploadProductModel.getProductCategoryList();
        pcondition = userUploadProductModel.getProductCondition();
        String serviceName = userUploadProductModel.getServiceName();
        String serviceCategory = userUploadProductModel.getServiceCategory();
        String serviceDescription = userUploadProductModel.getServiceDescription();
        String serviceEstimatedMarketValue = userUploadProductModel.getServiceEstimatedMarketValue();
        String servicePossibleExchangeWith = userUploadProductModel.getServicePossibleExchangeWith();
        String serviceImageUri = userUploadProductModel.getServiceImageUri();
        String tag = userUploadProductModel.getTag();
        UserModel postedBy = userUploadProductModel.getPostedBy();
        ArrayList<String> pimagelist = userUploadProductModel.getmArrList();


        pimg = userUploadProductModel.getmImageUri();

        intent.putExtra("name", pname);
        intent.putExtra("desc", pdesc);
        intent.putExtra("ad_id", ad_id);
        intent.putExtra("exchange", pexch);
        intent.putExtra("est", pest);
        intent.putExtra("type", ptype);
        intent.putExtra("category", pcategory);
        intent.putExtra("condition", pcondition);
        intent.putExtra("exch", pexch);
        intent.putExtra("worth", pest);
        intent.putExtra("Key", myCurrentDateTime);
        intent.putExtra("myCurrentDateTimeString", myCurrentDateTimeString);
        intent.putExtra("serviceName", serviceName);
        intent.putExtra("serviceCategory", serviceCategory);
        intent.putExtra("serviceDescription", serviceDescription);
        intent.putExtra("serviceEstimatedMarketValue", serviceEstimatedMarketValue);
        intent.putExtra("servicePossibleExchangeWith", servicePossibleExchangeWith);
        intent.putExtra("serviceImageUri", serviceImageUri);
        intent.putExtra("tag", tag);
        intent.putExtra("imagelist", pimagelist);
        intent.putExtra("image", pimg);
        intent.putExtra("user", postedBy);
        startActivity(intent);

    }

    public void search(final String query) {
        text_search.setVisibility(View.GONE);
        searchbar.setMaxWidth(1000);
        DatabaseReference searchRef;
        searchRef = FirebaseDatabase.getInstance().getReference("ProductsAndServices");
        searchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userlist.clear();
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    UserUploadProductModel searchedusers = usersSnapshot.getValue(UserUploadProductModel.class);
                    if (Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE).matcher(searchedusers.getProductName()).find()) {
                        userlist.add(searchedusers);
                    }
                }
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}

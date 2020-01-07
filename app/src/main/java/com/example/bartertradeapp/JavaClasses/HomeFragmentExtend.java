package com.example.bartertradeapp.JavaClasses;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.DetailedActivity;
import com.example.bartertradeapp.R;
import com.example.bartertradeapp.adapters.custom_list_adapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragmentExtend extends BaseFragment implements custom_list_adapter.ItemClickListener{

    private RecyclerView recyclerView;
    private custom_list_adapter adapter;
    LinearLayoutManager layoutManager;

    FirebaseAuth uploadAuth;
    List<UserUploadProductModel> userlist;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_fragment_extend,container,false);


        uploadAuth = FirebaseAuth.getInstance();
        userlist = new ArrayList<>();
        // hardcode adding items to list for testing reasons
        //userlist.add(new UserUploadProductModel("Shayan","pagaal hy shayan","good","asdasdsadasdadasd"));

        // set up the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView22);
        //layoutManager = new LinearLayoutManager(getContext());
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        // initializing adapter
        adapter = new custom_list_adapter (getContext(), userlist);
        adapter.setClickListener(this);


        //databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("UserUploadProducts");
        viewDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child("UserUploadProducts")
                .child(uploadAuth.getUid());

        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userlist.clear();
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()){
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
        pname = userUploadProductModel.getProductName();
        pdesc = userUploadProductModel.getProductDescription();
        pexch = userUploadProductModel.getPossibleExchangeWith();
        pest = userUploadProductModel.getProductEstimatedMarketValue();
        ptype = userUploadProductModel.getProductType();
        pcategory = userUploadProductModel.getProductCategoryList();
        pcondition = userUploadProductModel.getProductCondition();
        ArrayList<String> pimagelist = userUploadProductModel.getmArrList();


        pimg = userUploadProductModel.getmImageUri();

        intent.putExtra("name", pname);
        intent.putExtra("desc", pdesc);

        intent.putExtra("exchange", pexch);
        intent.putExtra("est", pest);
        intent.putExtra("type", ptype);
        intent.putExtra("category", pcategory);
        intent.putExtra("condition", pcondition);
        intent.putExtra("exch", pexch);
        intent.putExtra("worth", pest);
        intent.putExtra("imagelist", pimagelist);
        intent.putExtra("image", pimg);
        startActivity(intent);
        //Toast.makeText(getContext(), "as"+image, Toast.LENGTH_SHORT).show();

        // ye 9.37pm pr likha hy
    }
}

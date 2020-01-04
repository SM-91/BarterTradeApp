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

public class HomeFragment extends BaseFragment implements custom_list_adapter.ItemClickListener{

    private RecyclerView recyclerView, recyclerView2,recyclerView3;
    private custom_list_adapter adapter, adapter2, adapter3;
    LinearLayoutManager layoutManager, layoutManager2,layoutManager3;

    FirebaseAuth uploadAuth;
    List<UserUploadProductModel> userlist,userlist2,userlist3;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);


        userlist = new ArrayList<>();
        userlist2 = new ArrayList<>();
        userlist3 = new ArrayList<>();
        // hardcode adding items to list for testing reasons
        //userlist.add(new UserUploadProductModel("Shayan","pagaal hy shayan","good","asdasdsadasdadasd"));

        // set up the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView2 = view.findViewById(R.id.recyclerView2);
        recyclerView3 = view.findViewById(R.id.recyclerView3);
        //layoutManager = new LinearLayoutManager(getContext());
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager3 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView3.setLayoutManager(layoutManager3);
        // initializing adapter
        adapter = new custom_list_adapter (getContext(), userlist);
        adapter.setClickListener(this);

        adapter2 = new custom_list_adapter (getContext(), userlist2);
        adapter2.setClickListener(this);

        adapter3 = new custom_list_adapter (getContext(), userlist3);
        adapter3.setClickListener(this);


        databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("UserUploadProducts");
        databaseReference.addValueEventListener(new ValueEventListener() {
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

        databaseReference = FirebaseDatabase.getInstance().getReference().child("zNDkFxEHhKa56XNRSgExnhG4N7F3").child("UserUploadProducts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userlist2.clear();
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()){
                    UserUploadProductModel users = usersSnapshot.getValue(UserUploadProductModel.class);
                    userlist2.add(users);
                }
                // setting adapter to recycler View
                //recyclerView.setAdapter(adapter);
                recyclerView2.setAdapter(adapter2);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("UserUploadProducts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userlist3.clear();
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()){
                    UserUploadProductModel users = usersSnapshot.getValue(UserUploadProductModel.class);
                    userlist3.add(users);
                }
                // setting adapter to recycler View
                //recyclerView.setAdapter(adapter);
                recyclerView3.setAdapter(adapter3);

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

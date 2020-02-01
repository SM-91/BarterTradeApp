package com.example.bartertradeapp.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bartertradeapp.DataModels.BidRequestModel;
import com.example.bartertradeapp.DataModels.RequestModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.R;
import com.example.bartertradeapp.adapters.BidRequestAdapter;
import com.example.bartertradeapp.adapters.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestListFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView rvRequestList;
    private List<UserModel> otherUserList = new ArrayList<>();
    private Map<String, Boolean> otherUserMap = new HashMap<>();
    private ArrayList<BidRequestModel> requestAdapterModelList = new ArrayList<>();

    private String myId;
    private String requestId;
    private String ad_id = " ";
    private String name;
    private boolean accepted;
    UserModel sender, reciever;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_list, container, false);

        myId = FirebaseAuth.getInstance().getUid();
        rvRequestList = view.findViewById(R.id.rvRequestList);
        rvRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseReference getProductAdIdReference = FirebaseDatabase.getInstance().getReference("Requests");
        //Show Progress Dialog
        getProductAdIdReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //otherUserMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String productId = snapshot.getKey();
                    if (!TextUtils.isEmpty(productId)) {
                        ad_id = productId;
                        getProduct();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    private void getProduct() {
        DatabaseReference productReference = FirebaseDatabase.getInstance().getReference("ProductsAndServices").child(ad_id);
        productReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserUploadProductModel productModel = dataSnapshot.getValue(UserUploadProductModel.class);
                if(productModel != null) {
                    getUser(productModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUser(final UserUploadProductModel productModel) {
        DatabaseReference allRequestsReference = FirebaseDatabase.getInstance().getReference("Requests").child(ad_id);
        //Show Progress Dialog
        allRequestsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //otherUserList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RequestModel requestModel = snapshot.getValue(RequestModel.class);
                    UserModel otherUser = null;

                    sender = requestModel.getSender();
                    reciever = requestModel.getReciever();
                    accepted = requestModel.isAccepted();
                    name = requestModel.getName();
                    requestId = requestModel.getRequestId();

                    /*if (myId.equals(requestModel.getSender().getuserId())) {
                        otherUser = requestModel.getReciever();
                    } else*/
                    if (myId.equals(requestModel.getReciever().getuserId())) {
                        otherUser = requestModel.getSender();
                    }

                    if (otherUser != null) {
                        if (!otherUserMap.containsKey(otherUser.getuserId())) {
                            otherUserMap.put(otherUser.getuserId(), true);
                            otherUserList.add(otherUser);

                            BidRequestModel requestAdapterModel = new BidRequestModel();
                            requestAdapterModel.setUserModel(otherUser);
                            requestAdapterModel.setRequestModel(requestModel);
                            requestAdapterModel.setProductModel(productModel);
                            requestAdapterModelList.add(requestAdapterModel);
                        }
                    }
                }
                setRVAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setRVAdapter() {
        BidRequestAdapter bidRequestAdapter = new BidRequestAdapter(requestAdapterModelList);
        bidRequestAdapter.setOnClickListener(this);
        rvRequestList.setAdapter(bidRequestAdapter);
        //userAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        DatabaseReference setAcceptedReference;
        setAcceptedReference = FirebaseDatabase.getInstance().getReference("Requests")
                .child(ad_id);

        RequestModel requestModel = new RequestModel();
        requestModel.setAdId(ad_id);
        requestModel.setAccepted(true);
        requestModel.setSender(sender);
        requestModel.setReciever(reciever);
        requestModel.setName(name);
        requestModel.setRequestId(requestId);
        setAcceptedReference.child(requestId).setValue(requestModel);


      /*  UserModel clickedUserModel = (UserModel) v.getTag();
        Intent messageIntent = new Intent(MessageListActivity.this, MessageActivity.class);
        messageIntent.putExtra("user", clickedUserModel);
        messageIntent.putExtra("ad_id", ad_id);
        startActivity(messageIntent);*/
    }
}

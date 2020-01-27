package com.example.bartertradeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.bartertradeapp.DataModels.RequestModel;
import com.example.bartertradeapp.DataModels.UserModel;
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

public class RequestListActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView rvRequestList;
    private List<UserModel> otherUserList = new ArrayList<>();
    private Map<String, Boolean> otherUserMap = new HashMap<>();
    private String myId;
    private String requestId;
    private String ad_id = " ";
    private String name;
    private boolean accepted;
    UserModel sender,reciever;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_request_list);

        myId = FirebaseAuth.getInstance().getUid();
        rvRequestList = findViewById(R.id.rvRequestList);
        rvRequestList.setLayoutManager(new LinearLayoutManager(this));

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
                        getUser();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUser() {
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
        UserAdapter userAdapter = new UserAdapter(this, otherUserList);
        userAdapter.setOnClickListener(this);
        rvRequestList.setAdapter(userAdapter);
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

package com.example.bartertradeapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bartertradeapp.BaseActivity;
import com.example.bartertradeapp.DataModels.ChatModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.MessageActivity;
import com.example.bartertradeapp.R;
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

public class MessageListActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView rvMessageList;
    private List<UserModel> otherUserList = new ArrayList<>();
    private Map<String, Boolean> otherUserMap = new HashMap<>();
    private String myId;
    private String ad_id = " ";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        changeStatusBarColor();
        myId = FirebaseAuth.getInstance().getUid();
        rvMessageList = findViewById(R.id.rvMessageList);
        rvMessageList.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference getProductAdIdReference = FirebaseDatabase.getInstance().getReference("Messages");
        //Show Progress Dialog
        getProductAdIdReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //otherUserMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String conversationId = snapshot.getKey();
                    if (!TextUtils.isEmpty(conversationId)) {
                        ad_id = conversationId;
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
        DatabaseReference allChatReference = FirebaseDatabase.getInstance().getReference("Messages").child(ad_id);
        //Show Progress Dialog
        allChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //otherUserList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chatModel = snapshot.getValue(ChatModel.class);
                    UserModel otherUser = null;

                    if (myId.equals(chatModel.getSender().getuserId())) {
                        otherUser = chatModel.getReciever();
                    } else if (myId.equals(chatModel.getReciever().getuserId())) {
                        otherUser = chatModel.getSender();
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
        rvMessageList.setAdapter(userAdapter);
        //userAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        UserModel clickedUserModel = (UserModel) v.getTag();
        Intent messageIntent = new Intent(MessageListActivity.this, MessageActivity.class);
        messageIntent.putExtra("user", clickedUserModel);
        messageIntent.putExtra("ad_id", ad_id);
        startActivity(messageIntent);
    }
}

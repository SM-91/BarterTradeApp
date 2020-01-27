package com.example.bartertradeapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MessageListFragment extends BaseFragment implements View.OnClickListener{

    private RecyclerView rvMessageList;
    private List<UserModel> otherUserList = new ArrayList<>();
    private Map<String, Boolean> otherUserMap = new HashMap<>();
    private String myId;
    private String ad_id = " ";


    MessageFragment messageFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        myId = FirebaseAuth.getInstance().getUid();
        rvMessageList = view.findViewById(R.id.rvMessageList);
        rvMessageList.setLayoutManager(new LinearLayoutManager(getContext()));

        messageFragment = new MessageFragment();

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

        return view;
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
        UserAdapter userAdapter = new UserAdapter(getContext(), otherUserList);
        userAdapter.setOnClickListener(this);
        rvMessageList.setAdapter(userAdapter);
        //userAdapter.notifyDataSetChanged();

    }
    @Override
    public void onClick(View v) {
        UserModel clickedUserModel = (UserModel) v.getTag();
//        Intent messageIntent = new Intent(getContext(), MessageActivity.class);
//        messageIntent.putExtra("user", clickedUserModel);
//        messageIntent.putExtra("ad_id", ad_id);
//        startActivity(messageIntent);

        Bundle message = new Bundle();
        message.putString("ad_id", ad_id);
        message.putParcelable("user", clickedUserModel);
        messageFragment.setArguments(message);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_left);
        ft.replace(R.id.fragment_container, messageFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}

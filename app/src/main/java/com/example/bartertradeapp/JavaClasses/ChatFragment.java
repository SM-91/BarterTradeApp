package com.example.bartertradeapp.JavaClasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bartertradeapp.DataModels.ChatModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.R;
import com.example.bartertradeapp.adapters.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends BaseFragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<UserModel> mUsers = new ArrayList<>();

    FirebaseAuth uploadAuth;
    DatabaseReference reference;

    private List<String> stringList = new ArrayList<>();;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        uploadAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reference = FirebaseDatabase.getInstance().getReference("Users").child("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chatModel = snapshot.getValue(ChatModel.class);
                    UserModel otherUser = null;

                    if (uploadAuth.getUid().equals(chatModel.getSender())) {
                        otherUser = chatModel.getReciever();
                    }else if (uploadAuth.getUid().equals(chatModel.getReciever())) {
                       otherUser = chatModel.getSender();
                    }

                    if(otherUser != null){
                        stringList.add(String.valueOf(otherUser));
                    }

                }

                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void readChats() {
        mUsers = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);

                    for(String userid : stringList){
                        if (userid.equals(userModel.getuserId())){
                            if(mUsers.size() != 0){
                                for(UserModel userModel1 : mUsers){
                                    if(!userModel.getuserId().equals(userModel1.getuserId())){
                                        mUsers.add(userModel);
                                    }
                                }
                            } else {
                              mUsers.add(userModel);
                            }
                        }
                    }

                }

                userAdapter =  new UserAdapter(getContext(),mUsers);
                recyclerView.setAdapter(userAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

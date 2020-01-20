package com.example.bartertradeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.bartertradeapp.DataModels.ChatModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.adapters.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MessageActivity extends AppCompatActivity {

    FirebaseAuth chatAuth;
    private DatabaseReference recieverDatabaseReference, chatReference;


    ImageButton btn_send;
    EditText text_send;

    String userid = "";
    String recieverId = "";
//    String pushKey = "";

    MessageAdapter messageAdapter;
    List<ChatModel> chatModels = new ArrayList<>();

    RecyclerView recyclerView;

    private UserModel sender, receiver;

    /**
     * Steps:
     * pushKey => product_id for every conversation
     * 0.5) humay saray userIDs ki list chaiye hgi <-
     * 1) Saray messages get kar liye (message, sender, reciever)
     * 2) Us mai apnay walay messages filter kar liyay
     * 3) list -> humaray (user1) messages with everyone (user2, user3, user4 etc)
     * 4) sender ya receiver ki ID ko userIDs ki list se map kar k ek separate list ban jaye gi conversations ki
     * <p>
     * SHayan is user1
     * Adeel is user2
     * Faran is user3
     * Moteeb is user4
     * <p>
     * user1 is login (shayan)
     * shayan opens messages
     * <p>
     * INTEHAYI BADTEMEEZANA OR SASTI APPROACH
     * <p>
     * Message -> message, sender (not ID. full information of User), receiver (not ID. full information of User)
     * <p>
     * <p>
     * 2) get full list of messages -> listOfMessages -> pushkey = product_id.
     * 3) Message contains (message, sender, reciever)
     * 4) listOfMessages -> singleMessage jiska sender/receiver was equal to Shayan's ID.
     * 5) Us message ka humnay dusri ID get kar li.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            receiver = bundle.getParcelable("user");
        }

        chatAuth = FirebaseAuth.getInstance();

        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(msg);
                } else {
                        Toast.makeText(MessageActivity.this, "You cannot send empty message", Toast.LENGTH_LONG).show();
                }
                text_send.setText("");
            }
        });

        recieverDatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child("UserDetails");
        recieverDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);

                    if (userModel.getuserId().equals(chatAuth.getUid())) {
                        sender = userModel;
                        readMessage();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", this.sender);
        hashMap.put("reciever", this.receiver);
        hashMap.put("message", message);

        DatabaseReference sendMessageReference =
                FirebaseDatabase.getInstance().getReference("Users").child("Chat").push();
        sendMessageReference.setValue(hashMap);
    }


    private void readMessage() {
        chatReference = FirebaseDatabase.getInstance().getReference("Users").child("Chat");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatModels.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chatModel = snapshot.getValue(ChatModel.class);
                    if (chatModel != null) {
                        if ((sender.getuserId().equals(chatModel.getReciever().getuserId()) && receiver.getuserId().equals(chatModel.getSender().getuserId()))
                                || (receiver.getuserId().equals(chatModel.getReciever().getuserId()) && sender.getuserId().equals(chatModel.getSender().getuserId()))) {
                            chatModels.add(chatModel);
                        }


                        if (messageAdapter == null) {
                            messageAdapter = new MessageAdapter(MessageActivity.this, chatModels);
                            recyclerView.setAdapter(messageAdapter);
                        } else {
                            messageAdapter.notifyDataSetChanged();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}






//String recieverKey = recieverDatabaseReference.getKey();
        /**/




/*
        DatabaseReference userIDReference = FirebaseDatabase.getInstance().getReference("Users")
                .child("Chat");
        userIDReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MessageActivity", "onDataChange called");

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    UserModel userModel = snapshot.getValue(UserModel.class);

                    if(!chatAuth.getUid().equals(userModel.getuserId())){
                        recieverId = userModel.getuserId();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
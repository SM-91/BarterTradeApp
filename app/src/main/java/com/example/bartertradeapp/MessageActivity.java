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

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<ChatModel> chatModels = new ArrayList<>();

    RecyclerView recyclerView;

    private UserModel sender, receiver;
    private String ad_id ,  food_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            ad_id = bundle.getString("ad_id");
            food_name = bundle.getString("food_name");
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

        DatabaseReference recieverDatabaseReference;
        recieverDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
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

        ChatModel chatModel = new ChatModel();
        chatModel.setConversationID(ad_id);
        chatModel.setFoodName(food_name);
        chatModel.setReciever(this.receiver);
        chatModel.setSender(this.sender);
        chatModel.setMessage(message);

        DatabaseReference sendMessageReference =
                FirebaseDatabase.getInstance().getReference("Messages");
        sendMessageReference.child(ad_id).push().setValue(chatModel);
    }

    private void readMessage() {
        DatabaseReference chatReference;
        chatReference = FirebaseDatabase.getInstance().getReference("Messages").child(ad_id);
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

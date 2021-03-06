package com.example.bartertradeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.bartertradeapp.DataModels.ChatModel;
import com.example.bartertradeapp.DataModels.RatingModel;
import com.example.bartertradeapp.DataModels.UserHistoryModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.adapters.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.View.GONE;

public class MessageActivity extends BaseActivity {

    FirebaseAuth chatAuth;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<ChatModel> chatModels = new ArrayList<>();

    RecyclerView recyclerView;

    private UserModel sender, receiver;
    private String ad_id ,  product_name, category;

    LinearLayout feedback_layout,trade_layout;
    private RadioButton radio_btn_no,radio_btn_yes,trade_yes,trade_no;
    private RadioGroup radioGroup,tradeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        changeStatusBarColor();

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            ad_id = bundle.getString("ad_id");
            product_name = bundle.getString("product_name");
            category = bundle.getString("category");
            receiver = bundle.getParcelable("user");
        }

        chatAuth = FirebaseAuth.getInstance();

        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        radio_btn_no = findViewById(R.id.btn_radio_no);
        radio_btn_yes = findViewById(R.id.btn_radio_yes);
        trade_yes = findViewById(R.id.btn_radio_trade_yes);
        trade_no = findViewById(R.id.btn_radio_trade_no);
        feedback_layout = findViewById(R.id.feedback_ask);
        trade_layout = findViewById(R.id.trade_done);
        radioGroup = findViewById(R.id.radiogroup_yesno);
        tradeGroup = findViewById(R.id.radiogroup_trade_yes);

        recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_btn_no.isChecked()) {
                    feedback_layout.setVisibility(GONE);
                } else if (radio_btn_yes.isChecked()){
                    radio_btn_no.setChecked(false);
                    finish();
                    Intent intent = new Intent(MessageActivity.this, FeedbackActivity.class);
                    intent.putExtra("image_data", sender.getUserImageUrl());
                    intent.putExtra("id_data", receiver.getuserId());
                    intent.putExtra("postername_data", sender.getUserName());
                    startActivity(intent);
                    //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Feedback_postFragment()).addToBackStack(null).commit();
                }
            }
        });

        tradeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (trade_no.isChecked()) {
                    feedback_layout.setVisibility(GONE);
                    trade_layout.setVisibility(GONE);
                } else if (trade_yes.isChecked()){
                    radio_btn_no.setChecked(false);
                    postUserHistory();
                    trade_layout.setVisibility(GONE);
                    feedback_layout.setVisibility(View.VISIBLE);
                }
            }
        });

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

        getSender();
        getFeedBack();
    }

    private void sendMessage(String message) {

        ChatModel chatModel = new ChatModel();
        chatModel.setConversationID(ad_id);
        chatModel.setName(product_name);
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

    private void getSender(){
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

    private void getFeedBack(){
        DatabaseReference feedbackDatabaseReference;
        feedbackDatabaseReference = FirebaseDatabase.getInstance().getReference("UserFeedback").child(receiver.getuserId());
        feedbackDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RatingModel list = snapshot.getValue(RatingModel.class);

                    if (list.getBuyerid().equals(chatAuth.getUid())) {
                        //Toast.makeText(MessageActivity.this, ""+chatAuth.getUid(), Toast.LENGTH_SHORT).show();
                        feedback_layout.setVisibility(GONE);
                        trade_layout.setVisibility(GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void postUserHistory(){

        String dateAndTime = DateFormat.getDateTimeInstance()
                .format(Calendar.getInstance().getTime());

        UserHistoryModel userHistoryModel = new UserHistoryModel();
        userHistoryModel.setAdId(ad_id);
        userHistoryModel.setName(product_name);
        userHistoryModel.setCategory(category);
        userHistoryModel.setReciever(this.receiver);
        userHistoryModel.setSender(this.sender);
        userHistoryModel.setDateAndTime(dateAndTime);

        DatabaseReference userHistoryReference;
        userHistoryReference = FirebaseDatabase.getInstance().getReference("UserHistory")
                .child(sender.getuserId()).child(ad_id);
        userHistoryReference.setValue(userHistoryModel);
    }
}

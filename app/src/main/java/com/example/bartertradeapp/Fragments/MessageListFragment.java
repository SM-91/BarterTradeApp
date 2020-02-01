package com.example.bartertradeapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bartertradeapp.DataModels.ChatModel;
import com.example.bartertradeapp.DataModels.CustomModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;

import com.example.bartertradeapp.MessageActivity;
import com.example.bartertradeapp.MessageListActivity;
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

import static com.example.bartertradeapp.HomeActivity.msg_category;
import static com.example.bartertradeapp.HomeActivity.msg_id;
import static com.example.bartertradeapp.HomeActivity.msg_product_name;




public class MessageListFragment extends BaseFragment implements View.OnClickListener{

    private RecyclerView rvMessageList;
    private List<UserModel> otherUserList = new ArrayList<>();
    private Map<String, Boolean> otherUserMap = new HashMap<>();
    private ArrayList<CustomModel> customModelArrayList = new ArrayList<>();
    private String myId;
    private String ad_id = " ";

    private String tag;


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
                        getProduct();
                        getProductDetails();
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
                if (productModel != null) {
                    getUser(productModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUser(final UserUploadProductModel productModel) {
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

                            CustomModel requestAdapterModel = new CustomModel();
                            requestAdapterModel.setUserModel(otherUser);
                            requestAdapterModel.setChatModel(chatModel);
                            requestAdapterModel.setUserUploadProductModel(productModel);
                            customModelArrayList.add(requestAdapterModel);
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
        UserAdapter userAdapter = new UserAdapter(getContext(), customModelArrayList);
        userAdapter.setOnClickListener(this);
        rvMessageList.setAdapter(userAdapter);
        //userAdapter.notifyDataSetChanged();

    }

    private void getProductDetails(){

        DatabaseReference productDetailsRef;
        productDetailsRef = FirebaseDatabase.getInstance().getReference("ProductsAndServices")
                .child(ad_id);
        productDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserUploadProductModel userUploadProductModel = dataSnapshot.getValue(UserUploadProductModel.class);
                tag = userUploadProductModel.getTag();
                if(tag.equals("Product")){
                    msg_category = userUploadProductModel.getProductCategoryList();
                    msg_product_name = userUploadProductModel.getProductName();
                }else {
                    msg_category = userUploadProductModel.getServiceCategory();
                    msg_product_name = userUploadProductModel.getServiceName();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        UserModel clickedUserModel = (UserModel) v.getTag();
//        Intent messageIntent = new Intent(getContext(), MessageActivity.class);
//        messageIntent.putExtra("user", clickedUserModel);
//        messageIntent.putExtra("ad_id", ad_id);
//        startActivity(messageIntent);

//        Bundle message = new Bundle();
//        message.putString("ad_id", ad_id);
//        message.putParcelable("user", clickedUserModel);
//        messageFragment.setArguments(message);

        CustomModel clickedCustomModel = (CustomModel) v.getTag();
      /*  Intent messageIntent = new Intent(MessageListActivity.this, MessageActivity.class);
        messageIntent.putExtra("user", clickedCustomModel.getUserModel());
        messageIntent.putExtra("ad_id", ad_id);
        startActivity(messageIntent);*/

        msg_temp = clickedCustomModel.getUserModel();
        msg_id =ad_id;

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_left);
        ft.replace(R.id.fragment_container, messageFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}

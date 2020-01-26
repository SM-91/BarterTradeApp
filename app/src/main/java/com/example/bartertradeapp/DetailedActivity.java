package com.example.bartertradeapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.example.bartertradeapp.DataModels.RequestModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.adapters.ViewPageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.internal.zzn;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailedActivity extends BaseActivity {

    TextView textView_name;
    TextView textView_desc;
    TextView textView_exch;
    TextView textView_est;
    TextView textView_type;
    TextView textView_category;
    TextView textView_condition;
    ImageView imageView;
    ViewPager viewPager;
    UserModel postedBy;
    String ad_id;
    String product_name;
    private UserModel sender, receiver;
    private boolean accepted;


    Button chatBtn, bidBtn;
    RelativeLayout relativeLayout;
    ViewPageAdapter adapter = null;

    FirebaseAuth uploadAuth;

    private ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    private ArrayList<String> listimages = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        changeStatusBarColor();

        chatBtn = findViewById(R.id.chatBtn);
        bidBtn = findViewById(R.id.bid);

        uploadAuth = FirebaseAuth.getInstance();

        textView_name = findViewById(R.id.textview_ad_name);
        textView_desc = findViewById(R.id.textview_ad_desc);
        textView_exch = findViewById(R.id.textview_ad_exchange);
        textView_est = findViewById(R.id.textview_ad_estimated);
        textView_type = findViewById(R.id.textview_ad_type);
        textView_category = findViewById(R.id.textview_ad_category);
        textView_condition = findViewById(R.id.textview_ad_condition);
        imageView = findViewById(R.id.image_display);

        relativeLayout = findViewById(R.id.detailsLayout);

        /*ViewPager*/
        viewPager = findViewById(R.id.view_pager_image);
        adapter = new ViewPageAdapter(this, mArrayUri);
        viewPager.getOffscreenPageLimit();
        viewPager.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        ad_id = getIntent().getStringExtra("ad_id");
        product_name = getIntent().getStringExtra("name");
        String desc = getIntent().getStringExtra("desc");
        String exch = getIntent().getStringExtra("exchange");
        String est = getIntent().getStringExtra("est");
        String type = getIntent().getStringExtra("type");
        String category = getIntent().getStringExtra("category");
        String condition = getIntent().getStringExtra("condition");
        String serviceName = getIntent().getStringExtra("serviceName");
        String serviceCategory = getIntent().getStringExtra("serviceCategory");
        String serviceDescription = getIntent().getStringExtra("serviceDescription");
        String serviceEstimatedMarketValue = getIntent().getStringExtra("serviceEstimatedMarketValue");
        String servicePossibleExchangeWith = getIntent().getStringExtra("servicePossibleExchangeWith");
        String serviceImageUri = getIntent().getStringExtra("serviceImageUri");
        String tag = getIntent().getStringExtra("tag");
        String myCurrentDataTimeString = getIntent().getStringExtra("myCurrentDateTimeString");
        Date myCurrentDataTime = getIntent().getParcelableExtra("Key");
        String mimage = getIntent().getStringExtra("image");
        receiver = getIntent().getParcelableExtra("user");
        listimages = getIntent().getStringArrayListExtra("imagelist");

        if (bundle != null) {

            if (tag.equals("Product")) {
                if (mimage != null) {
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.get().load(mimage)
                            .fit()
                            .into(imageView);
                } else if (!listimages.isEmpty()) {
                    viewPager.setVisibility(View.VISIBLE);
                    mArrayUri.clear();
                    for (int i = 0; i < listimages.size(); i++) {
                        Uri tem_uri = Uri.parse(listimages.get(i));
                        mArrayUri.add(tem_uri);
                    }
                    adapter.notifyDataSetChanged();
                }
                textView_name.setText(product_name);
                textView_desc.setText(desc);
                textView_exch.setText("Exchange with: " + exch);
                textView_est.setText("Estimated Price: " + est);
                textView_type.setText(type);
                textView_category.setText(category);
                textView_condition.setText(condition);
            } else {
                if (serviceImageUri != null) {
                    Picasso.get().load(serviceImageUri)
                            .fit()
                            .into(imageView);
                    imageView.setVisibility(View.VISIBLE);
                }
                textView_name.setText(serviceName);
                textView_desc.setText(serviceDescription);
                textView_exch.setText("Exchange with: " + servicePossibleExchangeWith);
                textView_est.setText("Estimated Price: " + serviceEstimatedMarketValue);
                textView_category.setText(serviceCategory);
                relativeLayout.setVisibility(View.GONE);
            }

        }
        /*Check the value of accepted*/
        getAccepted();

        DatabaseReference recieverDatabaseReference;
        recieverDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        recieverDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);

                    if (userModel.getuserId().equals(uploadAuth.getUid())) {
                        sender = userModel;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedActivity.this, MessageActivity.class);
                intent.putExtra("ad_id", ad_id);
                intent.putExtra("product_name", product_name);
                intent.putExtra("user", receiver);
                startActivity(intent);
            }
        });

        bidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference requestReference;
                RequestModel requestModel = new RequestModel();
                requestModel.setAdId(ad_id);
                requestModel.setReciever(receiver);
                requestModel.setSender(sender);
                requestModel.setName(product_name);
                requestModel.setAccepted(false);

                requestReference = FirebaseDatabase.getInstance().getReference("Requests").child(ad_id);
                String requestId = requestReference.push().getKey();
                requestModel.setRequestId(requestId);

                requestReference.child(requestId).setValue(requestModel);

                Toast.makeText(DetailedActivity.this, "Bid done", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getAccepted() {
        DatabaseReference allRequestsReference = FirebaseDatabase.getInstance().getReference("Requests").child(ad_id);
        //Show Progress Dialog
        allRequestsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //otherUserList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RequestModel requestModel = snapshot.getValue(RequestModel.class);
                    accepted = requestModel.isAccepted();

                    if(accepted = true){
                        bidBtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
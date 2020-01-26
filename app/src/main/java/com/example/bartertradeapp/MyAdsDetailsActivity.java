package com.example.bartertradeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.Fragments.UpdateProductFragment;
import com.example.bartertradeapp.Fragments.UpdateServiceFragment;
import com.example.bartertradeapp.adapters.ViewPageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class MyAdsDetailsActivity extends BaseActivity {

    FirebaseAuth uploadAuth;
    TextView text_name;
    TextView text_desc;
    TextView text_exch;
    TextView text_est;
    TextView text_type;
    TextView text_category;
    TextView text_condition;
    Button btnUpdate, btnDel;
    ImageView imageView;
    ViewPager viewPager;
    ViewPageAdapter adapter = null;
    RelativeLayout myRelativeLayout, productDetailsLayout;

    UserUploadProductModel userUploadProductModel;
    UpdateProductFragment updateProductFragment;
    UpdateServiceFragment updateServiceFragment;
    String ad_id;
    String user_id;
    private UserModel postedBy;
    String updateCurrentDateTime;
    Date myCurrentDataTime;

    private ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    private ArrayList<String> listimages = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads_details);
        changeStatusBarColor();
        uploadAuth = FirebaseAuth.getInstance();
        userUploadProductModel = new UserUploadProductModel();
        updateProductFragment = new UpdateProductFragment();
        updateServiceFragment = new UpdateServiceFragment();

        text_name = findViewById(R.id.textview_ad_name);
        text_desc = findViewById(R.id.textview_ad_desc);
        text_exch = findViewById(R.id.textview_ad_exchange);
        text_est = findViewById(R.id.textview_ad_estimated);
        text_type = findViewById(R.id.textview_ad_type);
        text_category = findViewById(R.id.textview_ad_category);
        text_condition = findViewById(R.id.textview_ad_condition);
        myRelativeLayout = findViewById(R.id.myAdsLayout);
        productDetailsLayout = findViewById(R.id.detailsLayout);


        btnUpdate = findViewById(R.id.btnUpdate);
        btnDel = findViewById(R.id.btnDelete);

        imageView = findViewById(R.id.myAdsDetailsImageView);
        /*ViewPager*/
        viewPager = findViewById(R.id.myAdsDetailsViewPager);
        adapter = new ViewPageAdapter(this, mArrayUri);
        viewPager.getOffscreenPageLimit();
        viewPager.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();

        String myCurrentDataTimeString = getIntent().getStringExtra("myCurrentDateTimeString");
        myCurrentDataTime = getIntent().getParcelableExtra("Key");
        ad_id = getIntent().getStringExtra("ad_id");
        String name = getIntent().getStringExtra("name");
        String desc = getIntent().getStringExtra("description");
        String exch = getIntent().getStringExtra("possibelExchange");
        String est = getIntent().getStringExtra("estimatedPrice");
        String type = getIntent().getStringExtra("type");
        String category = getIntent().getStringExtra("category");
        String condition = getIntent().getStringExtra("condition");
        String mimage = getIntent().getStringExtra("singleImage");
        String serviceName = getIntent().getStringExtra("serviceName");
        String serviceCategory = getIntent().getStringExtra("serviceCategory");
        String serviceDescription = getIntent().getStringExtra("serviceDescription");
        String serviceEstimatedMarketValue = getIntent().getStringExtra("serviceEstimatedMarketValue");
        String servicePossibleExchangeWith = getIntent().getStringExtra("servicePossibleExchangeWith");
        String serviceImageUri = getIntent().getStringExtra("serviceImageUri");
        String tag = getIntent().getStringExtra("tag");
        listimages = getIntent().getStringArrayListExtra("multipleImagesList");

        if (bundle != null) {

            if (tag.equals("Product")) {
                if (mimage != null) {
                    Picasso.get().load(mimage)
                            .fit()
                            .into(imageView);
                    imageView.setVisibility(View.VISIBLE);
                } else if (!listimages.isEmpty()) {
                    mArrayUri.clear();
                    for (int i = 0; i < listimages.size(); i++) {
                        Uri tem_uri = Uri.parse(listimages.get(i));
                        mArrayUri.add(tem_uri);
                    }
                    viewPager.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
                text_name.setText(name);
                text_desc.setText(desc);
                text_exch.setText("Exchange with: " + exch);
                text_est.setText("Estimated Price: " + est);
                text_type.setText(type);
                text_category.setText(category);
                text_condition.setText(condition);

            } else {
                if (serviceImageUri != null) {
                    Picasso.get().load(serviceImageUri)
                            .fit()
                            .into(imageView);
                    imageView.setVisibility(View.VISIBLE);
                }

                text_name.setText(serviceName);
                text_desc.setText(serviceDescription);
                text_exch.setText("Exchange with: " + servicePossibleExchangeWith);
                text_est.setText("Estimated Price: " + serviceEstimatedMarketValue);
                text_category.setText(serviceCategory);
                productDetailsLayout.setVisibility(View.GONE);
            }

        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref;
                ref = FirebaseDatabase.getInstance().getReference("UserUploads")
                        .child(uploadAuth.getUid()).child(ad_id);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String current_ad_id = dataSnapshot.child("adId").getValue(String.class);
                        String updateCurrentDateTimeString = dataSnapshot.child("currentDateTimeString").getValue(String.class);
                        String pName = dataSnapshot.child("productName").getValue(String.class);
                        String pDescription = dataSnapshot.child("productDescription").getValue(String.class);
                        String pPossibleExchangeWith = dataSnapshot.child("possibleExchangeWith").getValue(String.class);
                        String pEstimatedMarketValue = dataSnapshot.child("productEstimatedMarketValue").getValue(String.class);
                        String pType = dataSnapshot.child("productType").getValue(String.class);
                        String pCategory = dataSnapshot.child("productCategoryList").getValue(String.class);
                        String pCondition = dataSnapshot.child("productCondition").getValue(String.class);
                        String pImageUrl = dataSnapshot.child("mImageUri").getValue(String.class);
                        String tag = dataSnapshot.child("tag").getValue(String.class);
                        String serviceName = dataSnapshot.child("serviceName").getValue(String.class);
                        String serviceDescription = dataSnapshot.child("serviceDescription").getValue(String.class);
                        String servicePossibleExchangeWith = dataSnapshot.child("servicePossibleExchangeWith").getValue(String.class);
                        String serviceEstimatedMarketValue = dataSnapshot.child("serviceEstimatedMarketValue").getValue(String.class);
                        String serviceCategory = dataSnapshot.child("serviceCategory").getValue(String.class);
                        String serviceImageUri = dataSnapshot.child("serviceImageUri").getValue(String.class);
                        ArrayList<String> updateMultipleImageList = new ArrayList<>();

                        if (dataSnapshot.child("mArrList").getValue() != null) {
                            for (DataSnapshot listSnapshot : dataSnapshot.child("mArrList").getChildren()) {
                                updateMultipleImageList.add(listSnapshot.getValue(String.class));
                            }
                        }

                        if (postedBy != null) {
                            for (DataSnapshot postedBySnapshot : dataSnapshot.child("postedBy").getChildren()) {
                                user_id = postedBySnapshot.child("userId").getValue(String.class);
                            }
                        }

                        if (tag.equals("Product")) {
                            Bundle updateBundle = new Bundle();
                            updateBundle.putString("tag", tag);
                            updateBundle.putString("ad_id", current_ad_id);
                            updateBundle.putString("Key", updateCurrentDateTimeString);
                            updateBundle.putString("name", pName);
                            updateBundle.putString("desc", pDescription);
                            updateBundle.putString("exch", pPossibleExchangeWith);
                            updateBundle.putString("worth", pEstimatedMarketValue);
                            updateBundle.putString("image", pImageUrl);
                            updateBundle.putString("type", pType);
                            updateBundle.putString("category", pCategory);
                            updateBundle.putString("condition", pCondition);
                            updateBundle.putString("serviceImageUri", serviceImageUri);
                            updateBundle.putString("serviceName", serviceName);
                            updateBundle.putString("serviceDescription", serviceDescription);
                            updateBundle.putString("servicePossibleExchangeWith", servicePossibleExchangeWith);
                            updateBundle.putString("serviceEstimatedMarketValue", serviceEstimatedMarketValue);
                            updateBundle.putString("serviceCategory", serviceCategory);
                            updateBundle.putParcelable("postedBy", postedBy);
                            updateBundle.putString("user_id", user_id);
                            updateBundle.putStringArrayList("updateMultipleImagesList", updateMultipleImageList);
                            updateProductFragment.setArguments(updateBundle);

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            myRelativeLayout.setVisibility(View.GONE);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, updateProductFragment)
                                    .commit();
                        } else {

                            Bundle updateServiceBundle = new Bundle();
                            updateServiceBundle.putString("tag", tag);
                            updateServiceBundle.putString("ad_id", current_ad_id);
                            updateServiceBundle.putString("Key", updateCurrentDateTimeString);
                            updateServiceBundle.putString("serviceImageUri", serviceImageUri);
                            updateServiceBundle.putString("serviceName", serviceName);
                            updateServiceBundle.putString("serviceDescription", serviceDescription);
                            updateServiceBundle.putString("servicePossibleExchangeWith", servicePossibleExchangeWith);
                            updateServiceBundle.putString("serviceEstimatedMarketValue", serviceEstimatedMarketValue);
                            updateServiceBundle.putString("serviceCategory", serviceCategory);
                            updateServiceBundle.putParcelable("postedBy", postedBy);
                            updateServiceBundle.putString("user_id", user_id);
                            updateServiceFragment.setArguments(updateServiceBundle);

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            myRelativeLayout.setVisibility(View.GONE);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, updateServiceFragment)
                                    .commit();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference deleteDatabaseReference;
                deleteDatabaseReference = FirebaseDatabase.getInstance().getReference("UserUploads")
                        .child(uploadAuth.getCurrentUser().getUid()).child(ad_id);
                deleteDatabaseReference.removeValue();

                DatabaseReference allProductsDeleteReference;
                allProductsDeleteReference = FirebaseDatabase.getInstance().getReference("ProductsAndServices")
                        .child(ad_id);
                allProductsDeleteReference.removeValue();

                Toast.makeText(getApplicationContext(), "Product Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.bartertradeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.JavaClasses.UpdateProductFragment;
import com.example.bartertradeapp.JavaClasses.ViewPageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyAdsDetailsActivity extends AppCompatActivity {

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

    RelativeLayout myRelativeLayout;


    private ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    private ArrayList<String> listimages = new ArrayList<String>();

    UserUploadProductModel userUploadProductModel;
    private DatabaseReference ref;



    UpdateProductFragment updateProductFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads_details);

        uploadAuth = FirebaseAuth.getInstance();
        userUploadProductModel = new UserUploadProductModel();
        updateProductFragment = new UpdateProductFragment();

        text_name = findViewById(R.id.textview_ad_name);
        text_desc = findViewById(R.id.textview_ad_desc);
        text_exch = findViewById(R.id.textview_ad_exchange);
        text_est = findViewById(R.id.textview_ad_estimated);
        text_type = findViewById(R.id.textview_ad_type);
        text_category = findViewById(R.id.textview_ad_category);
        text_condition = findViewById(R.id.textview_ad_condition);
        myRelativeLayout = findViewById(R.id.myAdsLayout);


        btnUpdate = findViewById(R.id.btnUpdate);
        btnDel = findViewById(R.id.btnDelete);

        imageView = findViewById(R.id.myAdsDetailsImageView);
        /*ViewPager*/
        viewPager = findViewById(R.id.myAdsDetailsViewPager);
        adapter = new ViewPageAdapter(this, mArrayUri);
        viewPager.getOffscreenPageLimit();
        viewPager.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();

        final String myCurrentDataTime = getIntent().getStringExtra("Key");
        Toast.makeText(getApplicationContext(),myCurrentDataTime,Toast.LENGTH_LONG).show();
        final String name = getIntent().getStringExtra("name");
        final String desc = getIntent().getStringExtra("description");
        final String exch = getIntent().getStringExtra("possibelExchange");
        final String est = getIntent().getStringExtra("estimatedPrice");
        final String type = getIntent().getStringExtra("type");
        final String category = getIntent().getStringExtra("category");
        final String condition = getIntent().getStringExtra("condition");
        final String mimage = getIntent().getStringExtra("singleImage");
        listimages = getIntent().getStringArrayListExtra("multipleImagesList");

        if (bundle != null) {

            if (mimage != null) {
                Picasso.get().load(mimage)
                        .fit()
                        .centerCrop()
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
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ref = FirebaseDatabase.getInstance().getReference("Users").child("UserUploadProducts")
                        .child(uploadAuth.getUid()).child(myCurrentDataTime);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                        String updateCurrentDateTime = dataSnapshot.child("currentDateTime").getValue(String.class);

                        String pName = dataSnapshot.child("productName").getValue(String.class);
                        String pDescription = dataSnapshot.child("productDescription").getValue(String.class);
                        String pPossibleExchangeWith = dataSnapshot.child("possibleExchangeWith").getValue(String.class);
                        String pEstimatedMarketValue = dataSnapshot.child("productEstimatedMarketValue").getValue(String.class);
                        String pType = dataSnapshot.child("productType").getValue(String.class);
                        String pCategory = dataSnapshot.child("productCategoryList").getValue(String.class);
                        String pCondition = dataSnapshot.child("productCondition").getValue(String.class);
                        String pImageUrl = dataSnapshot.child("mImageUri").getValue(String.class);
                        ArrayList<String> updateMultipleImageList = new ArrayList<>();

                        if (dataSnapshot.child("mArrList").getValue() != null) {

                            for (DataSnapshot listSnapshot : dataSnapshot.child("mArrList").getChildren()) {
                                updateMultipleImageList.add(listSnapshot.child("mArrList").getValue(String.class));
                            }
                        }

                        Bundle updateBundle = new Bundle();
                        updateBundle.putString("Key", updateCurrentDateTime);
                        updateBundle.putString("name", pName);
                        updateBundle.putString("desc", pDescription);
                        updateBundle.putString("exch", pPossibleExchangeWith);
                        updateBundle.putString("worth", pEstimatedMarketValue);
                        updateBundle.putString("image", pImageUrl);
                        updateBundle.putString("type", pType);
                        updateBundle.putString("category", pCategory);
                        updateBundle.putString("condition", pCondition);
                        updateBundle.putStringArrayList("updateMultipleImagesList", updateMultipleImageList);
                        updateProductFragment.setArguments(updateBundle);

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        myRelativeLayout.setVisibility(View.GONE);
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, updateProductFragment)
                                .commit();

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
                deleteDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child("UserUploadProducts")
                        .child(uploadAuth.getCurrentUser().getUid()).child(myCurrentDataTime);
                deleteDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            DatabaseReference allProductsDeleteReference;
                            allProductsDeleteReference = FirebaseDatabase.getInstance().getReference("Users").child("AllProducts")
                                    .child(myCurrentDataTime);
                            allProductsDeleteReference.removeValue();
                            Toast.makeText(getApplicationContext(), "Product Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

            }
        });
    }
}

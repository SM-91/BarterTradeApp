/*
package com.example.bartertradeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.JavaClasses.ViewPageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.abhinay.input.CurrencyEditText;

public class UpdateProductActivity extends AppCompatActivity {

    FirebaseAuth uploadAuth;
    FirebaseDatabase firebaseDatabase;

    UserUploadProductModel userUploadProductModel;

    private static EditText et_name, et_description, et_possible_exchange_with;
    private static CurrencyEditText et_estimated_market_value;

    private Button btn_imageChange, btn_submit;

    private RadioGroup radioProductTypeGroup, radioProductConditionGroup;
    private RadioButton radioNew, radioUsed, radioGood, radioNormal;

    private Spinner productCategories;

    private ImageView imageView, updateImageView;

    private ArrayList<Uri> mArrayUri = new ArrayList<>();
    final ArrayList<String> arrayList = new ArrayList<>();

    private ArrayList<String> listImages = new ArrayList<>();
    String productSingleImage;

    private Uri mImageUri;

    ViewPager viewPager;
    ViewPageAdapter adapter = null;
    ViewPageAdapter updateImagesAdapter = null;


    ProgressDialog progressDialog;

    private static final int MULTIPLE_IMAGE_REQUEST = 2;
    int uploadCount = 0;

    ArrayAdapter<String> categoriesAdapter;
    private String[] categories = {"Clothes", "Shoes", "Household", "Electronics", "Console Games"};

    private String myCurrentDateTime = "";
    private String singleImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        userUploadProductModel = new UserUploadProductModel();

        uploadAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        if (uploadAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
        }

        Layout Components Casting


                EditText

        et_name = findViewById(R.id.et_productName);
        et_description = findViewById(R.id.et_productDescription);
        et_possible_exchange_with = findViewById(R.id.et_productPossibleExchange);
        et_estimated_market_value = findViewById(R.id.et_estimatedMarketValue);
        et_estimated_market_value.setCurrency("€");
        et_estimated_market_value.setDelimiter(false);
        et_estimated_market_value.setSpacing(false);
        et_estimated_market_value.setDecimals(true);
        //Make sure that Decimals is set as false if a custom Separator is used
        et_estimated_market_value.setSeparator(".");

        ViewPager

                viewPager = findViewById(R.id.viewPagerProduct);
        adapter = new ViewPageAdapter(getApplicationContext(), mArrayUri);
        viewPager.setAdapter(adapter);

        ImageView

                imageView = (ImageView) findViewById(R.id.tempImgView);
        updateImageView = findViewById(R.id.tempImgView2);

        RadioGroups

                radioProductTypeGroup = findViewById(R.id.radioProductTypeGroup);
        radioNew = findViewById(R.id.radioNew);
        radioUsed = findViewById(R.id.radioUsed);
        radioProductTypeListener();

        radioProductConditionGroup = findViewById(R.id.radioProductConditionGroup);
        radioGood = findViewById(R.id.radioGood);
        radioNormal = findViewById(R.id.radioNormal);
        radioProductConditionListener();

        ProgressDialog

                progressDialog = new ProgressDialog(getApplicationContext());

        Buttons

                btn_imageChange = findViewById(R.id.changeImageBtn);
        btn_imageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImagesFromGallery();
            }
        });

        btn_submit = findViewById(R.id.submitProductData);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mArrayUri.isEmpty()) {
                    uploadMultipleImages();
                } else if (mImageUri != null) {
                    uploadSingleImage();
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Spinner

                productCategories = findViewById(R.id.spinnerProductCategory);
        Spinner Listener

        categoriesAdapter = new ArrayAdapter<String>(
                getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        productCategories.setAdapter(categoriesAdapter);

        productCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();
                userUploadProductModel.setProductCategoryList(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/
/*

        Bundle from MyAdsDetailsActivity

        updateImagesAdapter = new ViewPageAdapter(getApplicationContext(), mArrayUri);
        viewPager.getOffscreenPageLimit();
        viewPager.setAdapter(updateImagesAdapter);

        Bundle updateBundle = getArguments();
        //id = getArguments().getString("AD_ID");
        //Log.i("ad id",id);
        myCurrentDateTime = getArguments().getString("Key");
        String productName = getArguments().getString("name");
        String productDescription = getArguments().getString("desc");
        String productPossibleExchangeWith = getArguments().getString("exch");
        String productEstimatedMarketPrice = getArguments().getString("worth");
        String productType = getArguments().getString("type");
        String productCondition = getArguments().getString("condition");
        String productCategory = getArguments().getString("category");
        productSingleImage = getArguments().getString("image");
        listImages = getArguments().getStringArrayList("updateMultipleImagesList");

        if (updateBundle != null) {
*//*


            if (productSingleImage == null || listImages.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please Select Images", Toast.LENGTH_LONG).show();
                finish();
            } else if (productSingleImage != null) {

                updateImageView.setVisibility(View.VISIBLE);
                Picasso.get().load(productSingleImage)
                        .fit()
                        .centerCrop()
                        .into(updateImageView);

            } else {
                viewPager.setVisibility(View.VISIBLE);
                mArrayUri.clear();
                try {
                    for (int i = 0; i < listImages.size(); i++) {
                        Uri tem_uri = Uri.parse(listImages.get(i));
                        mArrayUri.add(tem_uri);
                    }
                    updateImagesAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    // This will catch any exception, because they are all descended from Exception
                    System.out.println("Error " + e.getMessage());
                    Toast.makeText(getContext(), "Error in multiple images" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
            et_name.setText(productName);
            et_description.setText(productDescription);
            et_possible_exchange_with.setText(productPossibleExchangeWith);
            et_estimated_market_value.setText(productEstimatedMarketPrice);

            if (radioNew.isChecked()) {
                radioNew.setChecked(true);
            } else {
                radioUsed.setChecked(true);
            }

            if (radioGood.isChecked()) {
                radioGood.setChecked(true);
            } else {
                radioNormal.setChecked(true);
            }

            if (productCategory != null) {
                int spinnerPosition = categoriesAdapter.getPosition(productCategory);
                productCategories.setSelection(spinnerPosition);
            }
        }

    }
}
*/

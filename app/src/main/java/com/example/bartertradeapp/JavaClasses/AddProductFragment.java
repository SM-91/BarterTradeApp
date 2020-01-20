package com.example.bartertradeapp.JavaClasses;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.HomeActivity;
import com.example.bartertradeapp.LogInActivity;
import com.example.bartertradeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import me.abhinay.input.CurrencyEditText;

public class AddProductFragment extends BaseFragment {

 /*   DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth uploadAuth;*/

    private static EditText et_name, et_description, et_possible_exchange_with;
    private static CurrencyEditText et_estimated_market_value;


    private Button btn_imageChange, btn_submit;

    private RadioGroup radioProductTypeGroup, radioProductConditionGroup;
    private RadioButton radioNew, radioUsed, radioGood, radioNormal;

    private Spinner productCategories;

    private ImageView imageView;

    private ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    final ArrayList<String> arrayList = new ArrayList<>();
    private Uri mImageUri;

    private UserModel currentUserModel = null;

    public String key = " ";

    ViewPager viewPager;
    ViewPageAdapter adapter = null;


    ProgressDialog progressDialog;

    private static final int MULTIPLE_IMAGE_REQUEST = 2;
    int uploadCount = 0;
    private String[] categories = {"Clothes", "Shoes", "Household", "Electronics", "Console Games"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_product, container, false);

        userUploadProductModel = new UserUploadProductModel();

        uploadAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        if (uploadAuth.getCurrentUser() == null) {
            startActivity(new Intent(getContext(), LogInActivity.class));
        }

        /*Layout Components Casting*/

        /*EditText*/
        et_name = view.findViewById(R.id.et_productName);
        et_description = view.findViewById(R.id.et_productDescription);
        et_possible_exchange_with = view.findViewById(R.id.et_productPossibleExchange);
        et_estimated_market_value = view.findViewById(R.id.et_estimatedMarketValue);
        et_estimated_market_value.setCurrency("€");
        et_estimated_market_value.setDelimiter(false);
        et_estimated_market_value.setSpacing(false);
        et_estimated_market_value.setDecimals(true);
        //Make sure that Decimals is set as false if a custom Separator is used
        et_estimated_market_value.setSeparator(".");



        /*ViewPager*/
        viewPager = view.findViewById(R.id.viewPagerProduct);
        adapter = new ViewPageAdapter(getContext(), mArrayUri);
        viewPager.setAdapter(adapter);

        /*ImageView*/
        imageView = (ImageView) view.findViewById(R.id.tempImgView);

        /*RadioGroups*/
        radioProductTypeGroup = view.findViewById(R.id.radioProductTypeGroup);
        radioNew = view.findViewById(R.id.radioNew);
        radioUsed = view.findViewById(R.id.radioUsed);
        radioProductTypeListener();

        radioProductConditionGroup = view.findViewById(R.id.radioProductConditionGroup);
        radioGood = view.findViewById(R.id.radioGood);
        radioNormal = view.findViewById(R.id.radioNormal);
        radioProductConditionListener();

        /*ProgressDialog*/
        progressDialog = new ProgressDialog(getContext());

        /*Buttons*/
        btn_imageChange = view.findViewById(R.id.changeImageBtn);
        btn_imageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImagesFromGallery();
            }
        });

        btn_submit = view.findViewById(R.id.submitProductData);
        btn_submit.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString();
                String description = et_description.getText().toString();
                String estimated = et_estimated_market_value.getText().toString();
                String exchange = et_possible_exchange_with.getText().toString();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(estimated) && !TextUtils.isEmpty(exchange)){
                    if (!mArrayUri.isEmpty()) {
                        uploadMultipleImages();
                    } else if (mImageUri != null) {
                        uploadSingleImage();
                    } else {
                        Toast.makeText(getContext().getApplicationContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext().getApplicationContext(), "Please Write complete Details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*Spinner*/
        productCategories = view.findViewById(R.id.spinnerProductCategory);
        /*Spinner Listener*/
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_dropdown_item, categories);
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

        /*Fetching User Model From Firebase*/
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child("UserDetails");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);

                    if (uploadAuth.getUid().equals(userModel.getuserId())) {
                        currentUserModel = userModel;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void radioProductTypeListener() {
        radioProductTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioNew.isChecked()) {
                    userUploadProductModel.setProductType("New");
                } else {
                    userUploadProductModel.setProductType("Used");
                }
            }
        });
    }

    private void radioProductConditionListener() {
        radioProductConditionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioGood.isChecked()) {
                    userUploadProductModel.setProductCondition("Good");
                } else {
                    userUploadProductModel.setProductCondition("Normal");
                }
            }
        });
    }

    /*Intent Function for Open Gallery*/
    private void selectImagesFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), MULTIPLE_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MULTIPLE_IMAGE_REQUEST) {
            if (data.getClipData() != null) {
                mArrayUri.clear();
                imageView.setImageBitmap(null);
                int count = data.getClipData().getItemCount();
                Log.i("count", String.valueOf(count));
                int currentItem = 0;
                while (currentItem < count) {
                    mImageUri = data.getClipData().getItemAt(currentItem).getUri();
                    Log.i("uri", mImageUri.toString());
                    mArrayUri.add(mImageUri);
                    currentItem = currentItem + 1;

                }
                Log.i("listsize", String.valueOf(mArrayUri.size()));
                imageView.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);

                adapter.notifyDataSetChanged();
                /*mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });*/
            } else if (data.getData() != null) {
                mImageUri = data.getData();
                Bitmap new_bitmap = null;
                try {

                    new_bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mImageUri);
                    imageView.setImageBitmap(new_bitmap);
                    Picasso.get()
                            .load(mImageUri)
                            .fit()
                            .centerCrop()
                            .into(imageView);
                    viewPager.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /*Posting Functions*/
    private void uploadMultipleImages() {

        progressDialog = ProgressDialog.show(getContext(), "Posting Data",
                "Uploading..", true);

        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference("UserProductUploads").child("UserProductImages");
        Log.i("Checking Storage", String.valueOf(ImageFolder));

        arrayList.clear();
        for (uploadCount = 0; uploadCount < mArrayUri.size(); uploadCount++) {
            final Uri individualImage = mArrayUri.get(uploadCount);

            Log.i("Individual Image Uri:", String.valueOf(individualImage));
            final StorageReference imageName = ImageFolder.child("Image" + individualImage.getLastPathSegment());

            imageName.putFile(individualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = String.valueOf(uri);
                            Log.i("images Url", url);
                            arrayList.add(url);

                            if (arrayList.size() == mArrayUri.size()) {
                                uploadData();
                            }

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext().getApplicationContext(), "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    Log.i("Progress", "checking progress" + progress);
                    //progressDialog.setMessage("Uploaded" + (int) progress + "%");
                }
            });
        }


       /* Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {

            }
        };
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 10000);*/
    }

    private void uploadData() {
        userUploadProductModel.setmArrList(arrayList);
        progressDialog.cancel();
        storeLink();
    }

    private void uploadSingleImage() {

        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference("UserProductUploads").child("UserProductImages");
        Log.i("Checking Storage", String.valueOf(ImageFolder));

        final StorageReference singleImageName = ImageFolder.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
        singleImageName.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                singleImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String url = String.valueOf(uri);
                        Log.i("image Url", url);
                        userUploadProductModel.setmImageUri(url);
                        storeLink();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext().getApplicationContext(), "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                Log.i("Progress", "checking progress" + progress);
                progressDialog.setMessage("Uploaded" + (int) progress + "%");
            }
        });
    }


    /*Sending Data to DB*/
    private void storeLink() {

        final String myCurrentDateTime = DateFormat.getDateTimeInstance()
                .format(Calendar.getInstance().getTime());
        userUploadProductModel.setCurrentDateTime(myCurrentDateTime);


        userUploadProductModel.setProductName(et_name.getText().toString().trim());
        userUploadProductModel.setProductDescription(et_description.getText().toString().trim());
        userUploadProductModel.setProductEstimatedMarketValue(et_estimated_market_value.getText().toString().trim());
        userUploadProductModel.setPossibleExchangeWith(et_possible_exchange_with.getText().toString().trim());
        userUploadProductModel.setPostedBy(currentUserModel);


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("UserUploadProducts")
                .child(uploadAuth.getCurrentUser().getUid()).child(myCurrentDateTime);
        databaseReference.setValue(userUploadProductModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    DatabaseReference allProfuctRef = FirebaseDatabase.getInstance().getReference("Users").child("AllProducts")
                            .child(myCurrentDateTime);
                    allProfuctRef.setValue(userUploadProductModel);

                    Toast.makeText(getContext(),"Product Added",Toast.LENGTH_LONG).show();
                    //getActivity().finish();
                }

            }
        });



   /*     UpdateProductFragment updateProductFragment = new UpdateProductFragment();

        Bundle createBundle = new Bundle();
        createBundle.putString("Key", myCurrentDateTime);
        updateProductFragment.setArguments(createBundle);

        if (getActivity() != null)
            ((HomeActivity) getActivity()).loadFragment(updateProductFragment);*/



    }

    /*Posting End*/
}
package com.example.bartertradeapp.JavaClasses;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;
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
import java.util.ArrayList;

import me.abhinay.input.CurrencyEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProductFragment extends BaseFragment {

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


    private String singleImageUrl;
    String myCurrentDateTime;

    private UserModel currentUserModel = null;



    public UpdateProductFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_product, container, false);

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
        imageView = (ImageView) view.findViewById(R.id.tempImgView1);
        updateImageView = view.findViewById(R.id.tempImgView2);

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
                if (!mArrayUri.isEmpty()) {
                    uploadMultipleImages();
                } else if (mImageUri != null) {
                    uploadSingleImage();
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*Spinner*/
        productCategories = view.findViewById(R.id.spinnerProductCategory);
        /*Spinner Listener*/
        categoriesAdapter = new ArrayAdapter<String>(
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

    /*private void initEdittext() {

    }*/

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
            imageView.setVisibility(View.GONE);

            if (data.getClipData() != null) {
                mArrayUri.clear();
                //imageView.setImageBitmap(null);
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

                viewPager.setVisibility(View.VISIBLE);
                updateImageView.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
               /* mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });*/
            } else if (data.getData() != null) {
                mImageUri = data.getData();
                Bitmap new_bitmap = null;
                try {
//                    new_bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mImageUri);
//                    updateImageView.setImageBitmap(new_bitmap);
                    Picasso.get()
                            .load(mImageUri)
                            .fit()
                            .centerCrop()
                            .into(updateImageView);
                    viewPager.setVisibility(View.GONE);
                    updateImageView.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }


    @Override
    public void onStart() {
        super.onStart();

        /*Bundle from MyAdsDetailsActivity*/
        updateImagesAdapter = new ViewPageAdapter(getContext(), mArrayUri);
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

           /* if (productSingleImage == null || listImages.isEmpty()){
                Toast.makeText(getContext(),"Please Select Images",Toast.LENGTH_LONG).show();
                getActivity().finish();
            }*/
            if (productSingleImage != null) {

                updateImageView.setVisibility(View.VISIBLE);
                Picasso.get().load(productSingleImage)
                        .fit()
                        .centerCrop()
                        .into(updateImageView);

            } else {
                viewPager.setVisibility(View.VISIBLE);
                mArrayUri.clear();
                try{
                    for (int i = 0; i < listImages.size(); i++) {
                        Uri tem_uri = Uri.parse(listImages.get(i));
                        mArrayUri.add(tem_uri);
                    }
                    updateImagesAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    // This will catch any exception, because they are all descended from Exception
                    System.out.println("Error " + e.getMessage());
                    Toast.makeText(getContext(),"Error in multiple images" + e.getMessage(),Toast.LENGTH_SHORT).show();
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

    /*Posting Functions*/
    /*Posting Functions*/
    private void uploadMultipleImages() {

       /* progressDialog.setTitle("Uploading...");
        progressDialog.show();*/

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

                            userUploadProductModel.setmArrList(arrayList);
                            progressDialog.cancel();
                            storeLink();


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.cancel();
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

//        Runnable progressRunnable = new Runnable() {
//            @Override
//            public void run() {
//            }
//        };
//        Handler pdCanceller = new Handler();
//        pdCanceller.postDelayed(progressRunnable, 10000);
    }

    private void uploadSingleImage() {

        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference("UserProductUploads").child("UserProductImages");
        Log.i("Checking Storage", String.valueOf(ImageFolder));
      /*  if (mImageUri != null) {


        } else {
            Toast.makeText(getContext().getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }*/

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

        userUploadProductModel.setProductName(et_name.getText().toString().trim());
        userUploadProductModel.setProductDescription(et_description.getText().toString().trim());
        userUploadProductModel.setProductEstimatedMarketValue(et_estimated_market_value.getText().toString().trim());
        userUploadProductModel.setPossibleExchangeWith(et_possible_exchange_with.getText().toString().trim());
        userUploadProductModel.setPostedBy(currentUserModel);

        Bundle createBundle = getArguments();
        final String myCurrentDateTime = getArguments().getString("Key");

        if (createBundle != null) {


            userUploadProductModel.setCurrentDateTime(myCurrentDateTime);


            if (uploadAuth.getCurrentUser().getUid() != null) {
                updateDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child("UserUploadProducts")
                        .child(uploadAuth.getCurrentUser().getUid()).child(myCurrentDateTime);
                updateDatabaseReference.setValue(userUploadProductModel).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    /*if (singleImageUrl != null) {
                                        StorageReference storageReferenceDel = FirebaseStorage.getInstance()
                                                .getReferenceFromUrl(String.valueOf(mImageUri));
                                        storageReferenceDel.delete();
                                    } else {
                                        StorageReference storageReferenceDel = FirebaseStorage.getInstance()
                                                .getReferenceFromUrl(String.valueOf(mArrayUri));
                                        storageReferenceDel.delete();
                                    }

*/
                                    DatabaseReference newUpdateReference;
                                    newUpdateReference = FirebaseDatabase.getInstance().getReference("Users")
                                            .child("AllProducts").child(myCurrentDateTime);
                                    newUpdateReference.setValue(userUploadProductModel);
                                    Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT)
                                            .show();

                                    getActivity().finish();

                                }
                            }
                        }
                );
            }


        }

    }


}

package com.example.bartertradeapp.JavaClasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadServiceModel;
import com.example.bartertradeapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;

import me.abhinay.input.CurrencyEditText;

public class AddServiceFragment extends BaseFragment {

    private static ImageView serviceImageView;
    private static CurrencyEditText editTextPrice;
    private static EditText et_serviceName, et_serviceDescription, et_servicePossibleExchangeWith;
    private static Spinner serviceCategory;
    private Button btn_submit;

    private String[] serviceCategories = {"Tutoring", "Designing" ,"Electrical work", "Mechanical work", "Wood work", "Cleaning"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_service,container,false);

        userUploadServiceModel = new UserUploadServiceModel();
        userModel = new UserModel();
        uploadAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //refServiceImage = firebaseDatabase.getReference().child(firebaseAuth.getUid()).child("Profile");
        databaseReference = firebaseDatabase.getReference("Users").child("UserUploadProducts")
                .child(uploadAuth.getUid());
        //refServicePosting = firebaseDatabase.getReference().child(firebaseAuth.getUid()).child("UserUploadService");


        serviceImageView = view.findViewById(R.id.serviceGivenBy);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    UserModel userModel = userSnapshot.getValue(UserModel.class);
                    Picasso.get().load(userModel.getUserImageUrl())
                            .fit()
                            .centerCrop()
                            .into(serviceImageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        et_serviceName = view.findViewById(R.id.et_serviceName);
        et_serviceDescription = view.findViewById(R.id.et_serviceDescription);
        et_servicePossibleExchangeWith = view.findViewById(R.id.et_servicePossibleExchange);

        editTextPrice = (CurrencyEditText) view.findViewById(R.id.et_serviceEstimatedMarketValue);
        editTextPrice.setCurrency("€");
        editTextPrice.setDelimiter(false);
        editTextPrice.setSpacing(false);
        editTextPrice.setDecimals(true);
        //Make sure that Decimals is set as false if a custom Separator is used
        editTextPrice.setSeparator(".");

        btn_submit = view.findViewById(R.id.submitServiceData);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeLink();
            }
        });

        /*Spinner*/
        serviceCategory = view.findViewById(R.id.spinnerServiceCategory);
        /*Spinner Listener*/
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_dropdown_item, serviceCategories);
        serviceCategory.setAdapter(categoriesAdapter);

        serviceCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();
                userUploadServiceModel.setServiceCategory(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    private void initEditText(){

        userUploadServiceModel.setServiceName(et_serviceName.getText().toString().trim());
        userUploadServiceModel.setServiceDescription(et_serviceDescription.getText().toString().trim());
        userUploadServiceModel.setServicePossibleExchangeWith(et_servicePossibleExchangeWith.getText().toString().trim());
        userUploadServiceModel.setServiceEstimatedMarketValue(editTextPrice.getText().toString().trim());
    }

    private void storeLink(){

        String myCurrentDateTime = DateFormat.getDateTimeInstance()
                .format(Calendar.getInstance().getTime());

        String key = databaseReference.push().getKey();
        userUploadServiceModel.setUid(key);
        initEditText();
        databaseReference = firebaseDatabase.getReference("Users").child("UserUploadService")
                .child(uploadAuth.getUid()).child(myCurrentDateTime);
        databaseReference.setValue(userUploadProductModel);


        //refServicePosting.push().setValue(userUploadServiceModel);

        Toast.makeText(getContext().getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
    }
}

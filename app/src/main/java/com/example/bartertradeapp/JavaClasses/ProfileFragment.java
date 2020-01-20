package com.example.bartertradeapp.JavaClasses;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.LogInActivity;
import com.example.bartertradeapp.R;
import com.example.bartertradeapp.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends BaseFragment {


    private ImageView uImage;
    private EditText uName, uEmail, uBio, uAge;
    private RadioGroup uGender;
    private RadioButton uMale, uFemale;
    private Button btn_update;

    private Uri uImageUri;
    String image_url;


    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userModel = new UserModel();

        progressDialog = new ProgressDialog(getActivity());

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getContext() , LogInActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        uImage = view.findViewById(R.id.profile_image_view);
        uImage.setClickable(true);
        uName = view.findViewById(R.id.et_name);
        uEmail = view.findViewById(R.id.et_email);
        uAge = view.findViewById(R.id.et_age);
        uBio = view.findViewById(R.id.et_bio);
        uGender = view.findViewById(R.id.radioGroupGender);
        uMale = view.findViewById(R.id.radio_male);
        uFemale = view.findViewById(R.id.radio_female);
        btn_update = view.findViewById(R.id.submitProfileData);


        uRadioGroupCheckItem();

        uImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUserProfileImage();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updatingprofile();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // getting images
        viewDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child("UserDetails");
        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = usersSnapshot.getValue(UserModel.class);
                    uName.setText(userModel.getUserName());
                    uEmail.setText(userModel.getUserEmail());
                    Picasso.get().load(userModel.getUserImageUrl())
                            .fit()
                            .centerCrop()
                            .into(uImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uRadioGroupCheckItem() {
        uGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (uMale.isChecked()) {
                    userModel.setUserGender("Male");
                } else {
                    userModel.setUserGender("Female");
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                uImageUri = data.getData();
                gettingImageUrl();
                Picasso.get().load(uImageUri)
                        .fit()
                        .centerCrop()
                        .into(uImage);
            }
        }
    }

    public void gettingImageUrl(){
        StorageReference userProfileImage = FirebaseStorage.getInstance().getReference("UserProfilePic");

        if(uImageUri != null){

            final StorageReference singleImageName = userProfileImage.child(System.currentTimeMillis() + "." + "Image"+ uImageUri.getLastPathSegment());

            singleImageName.putFile(uImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    singleImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            image_url = String.valueOf(uri);
                            //Toast.makeText(SignUpActivity.this, "Image Url Saved"+image_url, Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(), "Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(getContext(), "Image not Saved", Toast.LENGTH_SHORT).show();
        }
    }


    private void updatingprofile() {

        userModel.setUserName(uName.getText().toString().trim());
        userModel.setUserEmail(uEmail.getText().toString().trim());
        userModel.setUserAge(uAge.getText().toString().trim());
        userModel.setUserBio(uBio.getText().toString().trim());
        userModel.setUserImageUrl(image_url);

            if (firebaseAuth.getCurrentUser().getUid() != null) {
                updateDatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
                        .child("UserDetails").child(firebaseAuth.getCurrentUser().getUid());
                updateDatabaseReference.setValue(userModel).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT)
                                            .show();
                                }
                                else {
                                    Toast.makeText(getContext(), "Rola hy reference me", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            }



    }
}

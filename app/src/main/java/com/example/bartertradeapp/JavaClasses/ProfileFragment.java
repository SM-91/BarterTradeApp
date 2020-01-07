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
import com.example.bartertradeapp.LogInActivity;
import com.example.bartertradeapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private Button uProfilePostData;

    private Uri uImageUri;


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
        uImageOnClick();

        uName = view.findViewById(R.id.et_name);
        uEmail = view.findViewById(R.id.et_email);
        uAge = view.findViewById(R.id.et_age);
        uBio = view.findViewById(R.id.et_bio);

        uGender = view.findViewById(R.id.radioGroupGender);
        uMale = view.findViewById(R.id.radio_male);
        uFemale = view.findViewById(R.id.radio_female);
        uRadioGroupCheckItem();

        uProfilePostData = view.findViewById(R.id.submitProfileData);
        uPostData();

        return view;
    }

    private void uImageOnClick() {
        uImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUserProfileImage();
            }
        });
    }

    private void uEditTextGetText() {
        userModel.setUserName(uName.getText().toString().trim());
        userModel.setUserEmail(uEmail.getText().toString().trim());
        userModel.setUserAge(uAge.getText().toString().trim());
        userModel.setUserBio(uBio.getText().toString().trim());
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

    private void uPostData() {
        uProfilePostData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postingProfileData();
            }
        });
    }

    private void postingProfileData() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child("Profile")
                .child(uploadAuth.getCurrentUser().getUid());

        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference userProfileImage = FirebaseStorage.getInstance().getReference(firebaseAuth.getUid()).child("Images").child("Profile Image");

        if(uImageUri != null){

            final StorageReference singleImageName = userProfileImage.child(System.currentTimeMillis() + "." + "Image"+ uImageUri.getLastPathSegment());

            singleImageName.putFile(uImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    singleImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = String.valueOf(uri);
                            userModel.setUserImageUrl(url);

                            uEditTextGetText();
                            databaseReference.push().setValue(userModel);

                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    Log.i("Progress","checking progress" + progress);
                    progressDialog.setMessage("Uploaded" + (int) progress + "%");
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data.getData() != null) {

                uImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uImageUri);
                    Picasso.get()
                            .load(uImageUri)
                            .fit()
                            .centerCrop()
                            .into(uImage);
                    uImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

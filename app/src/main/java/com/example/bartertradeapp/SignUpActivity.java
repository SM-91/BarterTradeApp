package com.example.bartertradeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SignUpActivity extends BaseActivity {

    EditText et_email,et_pass,et_confirm_pass,et_username;
    Button btn_signUp, btn_already_account,btn_pic;
    ImageView check;

    Intent intent;
    Uri uImageUri;
    String username,email,pass,confirm_pass,userid,userCreatedDateTime,image_url= null,ss;

    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        changeStatusBarColor();


        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(SignUpActivity.this);

        et_username = findViewById(R.id.reg_userName);
        et_email = findViewById(R.id.reg_email);
        et_pass = findViewById(R.id.reg_pass);
        et_confirm_pass = findViewById(R.id.reg_confirm_pass);

        check = findViewById(R.id.check_pic);

        btn_pic = findViewById(R.id.btn_pic_upload);
        btn_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUserProfileImage();
            }
        });

        btn_signUp = findViewById(R.id.reg_btn);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = et_email.getText().toString();
                //mUserModel.setUserEmail(email);

                username = et_username.getText().toString();
                //mUserModel.setUserName(username);

                pass = et_pass.getText().toString();
                confirm_pass = et_confirm_pass.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass) && !TextUtils.isEmpty(username)) {
                    if (pass.equals(confirm_pass)) {
                        if(uImageUri!=null) {
                            //reg_progress.setVisibility(View.VISIBLE);
                            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        //Toast.makeText(SignUpActivity.this, "Account Created.", Toast.LENGTH_LONG).show();

                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        userid = firebaseUser.getUid();

                                        userCreatedDateTime = DateFormat.getDateTimeInstance()
                                                .format(Calendar.getInstance().getTime());
                                        gettingImageUrl();
                                        progressDialog = ProgressDialog.show(SignUpActivity.this, "Creating Account",
                                                "Uploading Image", true);
                                        handler();
                                    } else {
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(SignUpActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                                    }
                                    // reg_progress.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        else{
                            Toast.makeText(SignUpActivity.this, "Select Profile Image", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(SignUpActivity.this, "Confirm Password and Password Field doesn't match.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Fill all fields", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_already_account = findViewById(R.id.reg_login_btn);
        btn_already_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                uImageUri = data.getData();
                check.setImageResource(R.drawable.ic_check_green_24dp);
            }
        }
        else {
            check.setImageResource(R.drawable.ic_check_red_24dp);
        }
    }

    //to check if user is already logged in
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }

    //send to MainActivity
    private void sendToMain() {
        intent = new Intent(SignUpActivity.this, HomeActivity.class);
        finish();
        startActivity(intent);
    }

    // User selecting profile pic (Profilefragment)
    public void selectUserProfileImage() {
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
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

                    Toast.makeText(SignUpActivity.this, "Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(SignUpActivity.this, "Image not Saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void pushing_data (){
        reference = FirebaseDatabase.getInstance().getReference("Users");

        //Toast.makeText(SignUpActivity.this, "url:"+image_url, Toast.LENGTH_SHORT).show();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("userId",userid);
        hashMap.put("userCreatedDateTime",userCreatedDateTime);
        hashMap.put("userName", username);
        hashMap.put("userEmail", email);
        hashMap.put("userImageUrl", image_url);

        reference.child(userid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void handler() {
        new Handler().postDelayed(new Runnable() {

            public void run() {
                pushing_data();
                progressDialog.dismiss();
                showLongToast(SignUpActivity.this, "Account Created");
            }

        }, 5000);

    }
}

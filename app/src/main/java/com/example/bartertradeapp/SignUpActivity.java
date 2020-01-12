package com.example.bartertradeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bartertradeapp.DataModels.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SignUpActivity extends BaseActivity {

    EditText et_email,et_pass,et_confirm_pass,et_username;
    Button btn_signUp, btn_already_account;

    UserModel mUserModel;

    private ProgressBar reg_progress;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        changeStatusBarColor();

        mAuth = FirebaseAuth.getInstance();

        mUserModel = new UserModel();

        et_username = findViewById(R.id.reg_userName);
        et_email = findViewById(R.id.reg_email);
        et_pass = findViewById(R.id.reg_pass);
        et_confirm_pass = findViewById(R.id.reg_confirm_pass);

        btn_signUp = findViewById(R.id.reg_btn);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = et_email.getText().toString();
                mUserModel.setUserEmail(email);

                final String username = et_username.getText().toString();
                mUserModel.setUserName(username);

                Log.i("User Email", email );
                String pass = et_pass.getText().toString();
                String confirm_pass = et_confirm_pass.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass)) {
                    if (pass.equals(confirm_pass)) {
                        //reg_progress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(SignUpActivity.this, "Account Created.", Toast.LENGTH_LONG).show();

                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    String userid = firebaseUser.getUid();

                                    String userCreatedDateTime = DateFormat.getDateTimeInstance()
                                            .format(Calendar.getInstance().getTime());

                                    reference = FirebaseDatabase.getInstance().getReference("Users")
                                            .child("UserDetails");

                                    HashMap<String,String> hashMap = new HashMap<>();
                                    hashMap.put("userId",userid);
                                    hashMap.put("userCreatedDateTime",userCreatedDateTime);
                                    hashMap.put("userName", username);
                                    hashMap.put("imageURL","default");

                                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Intent setupIntent = new Intent(SignUpActivity.this, HomeActivity.class);
                                                startActivity(setupIntent);
                                            }
                                        }
                                    });
                                } else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(SignUpActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                                }
                               // reg_progress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Confirm Password and Password Field doesn't match.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_already_account = findViewById(R.id.reg_login_btn);
        btn_already_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
            }
        });

        btn_already_account = findViewById(R.id.reg_login_btn);

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
        Intent mainIntent = new Intent(SignUpActivity.this, HomeActivity.class);
        startActivity(mainIntent);
        finish();
    }
}

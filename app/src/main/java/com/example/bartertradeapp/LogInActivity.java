package com.example.bartertradeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends BaseActivity {

    private FirebaseAuth mAuth;

    EditText et_login_email, et_login_password;
    Button btn_login,btn_signUp;

    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        changeStatusBarColor();
        mAuth = FirebaseAuth.getInstance();

        et_login_email = findViewById(R.id.reg_email);
        et_login_password = findViewById(R.id.reg_pass);

        btn_login = findViewById(R.id.login_btn);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progress = ProgressDialog.show(LogInActivity.this, "",
                        "Authenticating", true);

                Runnable progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String loginEmail = et_login_email.getText().toString().trim();
                        String loginPass = et_login_password.getText().toString().trim();

                        if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){
                            //set the visibility on for progressbar
                            loginProgress.setVisibility(View.VISIBLE);

                            mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){
                                        sendToMain();
                                    } else {
                                        //set the error message
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(LogInActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                                        progress.cancel();
                                    }
                                    //set the visibility off for progressbar
                                    loginProgress.setVisibility(View.INVISIBLE);
                                }
                            });

                        }
                        else{
                            Toast.makeText(LogInActivity.this, "Enter Email and Password", Toast.LENGTH_LONG).show();
                        }
                    }

                };
                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 1000);


            }
        });
        btn_signUp = findViewById(R.id.login_signUp_btn);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(regIntent);
            }
        });
        loginProgress = findViewById(R.id.login_progress);
    }

    //to check if user is currently loggeg in
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(LogInActivity.this, HomeActivity.class);
        startActivity(mainIntent);
        finish();
    }

}

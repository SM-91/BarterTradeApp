package com.example.bartertradeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.bartertradeapp.DataModels.RatingModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class FeedbackActivity extends BaseActivity {

    RatingBar rating_bar;
    TextView default_feedback;
    EditText user_feedback;
    Button btn_submit;
    RatingModel ratingdata;
    int rate;
    String RecieverUid,PosterImage,PosterName;
    UserModel reciever;
    FirebaseAuth uploadAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        rating_bar = findViewById(R.id.rating_bar);
        default_feedback = findViewById(R.id.text_feedback);
        user_feedback = findViewById(R.id.edittext_feedback);
        btn_submit = findViewById(R.id.submit_button);
        uploadAuth = FirebaseAuth.getInstance();

        ratingdata = new RatingModel();
        reciever = new UserModel();

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            RecieverUid = bundle.getString("id_data");
            PosterImage = bundle.getString("image_data");
            PosterName = bundle.getString("postername_data");
        }


        rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                rate = (int) rating;
                switch (rate) {
                    case 1:
                        default_feedback.setText("Poor");
                        break;
                    case 2:
                        default_feedback.setText("Below Average");
                        break;
                    case 3:
                        default_feedback.setText("Average");
                        break;
                    case 4:
                        default_feedback.setText("Good");
                        break;
                    case 5:
                        default_feedback.setText("Excellent");
                        break;
                }
            }


        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FeedbackActivity.this, "FeedBack Posted", Toast.LENGTH_SHORT).show();
                posting_data();
                passing_data();
            }
        });
    }

    private void posting_data() {

        String myCurrentDateTime = DateFormat.getDateTimeInstance()
                .format(Calendar.getInstance().getTime());
        //String Ad_id = uploadAuth.getCurrentUser().getUid();
        ratingdata.setCurrentDateTime(myCurrentDateTime);
        ratingdata.setRating(rate);
        ratingdata.setBuyerid(uploadAuth.getCurrentUser().getUid());
        ratingdata.setUserName(PosterName);
        ratingdata.setSellerid(RecieverUid);
        ratingdata.setFeedback(user_feedback.getText().toString());
        ratingdata.setImageUri(PosterImage);

        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("UserFeedback")
                .child(RecieverUid).child(uploadAuth.getCurrentUser().getUid());
        databaseReference.setValue(ratingdata);
    }


    private void passing_data() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        Intent intent = new Intent(FeedbackActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}


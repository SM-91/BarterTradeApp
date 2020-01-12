package com.example.bartertradeapp.JavaClasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bartertradeapp.DataModels.RatingModel;
import com.example.bartertradeapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class FeedbackFragment extends BaseFragment {

        RatingBar rating_bar;
        TextView default_feedback;
        EditText user_feedback;
        Button btn_submit;
        RatingModel ratingdata;
        int rate;

    @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_feedback,container,false);
            rating_bar = view.findViewById(R.id.rating_bar);
            default_feedback = view.findViewById(R.id.text_feedback);
            user_feedback = view.findViewById(R.id.edittext_feedback);
            btn_submit = view.findViewById(R.id.submit_button);
            uploadAuth = FirebaseAuth.getInstance();

            ratingdata = new RatingModel();


            rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                    rate = (int)rating;
                    switch (rate) {
                        case 1:
                            default_feedback.setText("Schlechte");
                            break;
                        case 2:
                            default_feedback.setText("unterdurchschnittlich");
                            break;
                        case 3:
                            default_feedback.setText("Durchschnittlich");
                            break;
                        case 4:
                            default_feedback.setText("gut");
                            break;
                        case 5:
                            default_feedback.setText("Ausgezeichnet");
                            break;
                    }
                }


            });


            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "need to post data from here to DB", Toast.LENGTH_SHORT).show();
                    postingdata();
                }
            });

            return view;
        }
    private void postingdata() {

        String myCurrentDateTime = DateFormat.getDateTimeInstance()
                .format(Calendar.getInstance().getTime());
        ratingdata.setCurrentDateTime(myCurrentDateTime);
        ratingdata.setRating(rate);
        ratingdata.setBuyerid(uploadAuth.getCurrentUser().getUid());
        ratingdata.setSellerid("asd");
        ratingdata.setFeedback(user_feedback.getText().toString());

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("UserRating")
                .child(myCurrentDateTime);
        databaseReference.setValue(ratingdata);



       /*         getReference(uploadAuth.getUid()).child("");


        initEdittext();
        databaseReference.push().setValue(userUploadProductModel);*/
    }

    }
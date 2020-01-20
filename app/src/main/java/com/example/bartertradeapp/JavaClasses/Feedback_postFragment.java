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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bartertradeapp.DataModels.RatingModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class Feedback_postFragment extends BaseFragment {


        RatingBar rating_bar;
        TextView default_feedback;
        EditText user_feedback;
        Button btn_submit;
        RatingModel ratingdata;
        int rate;
        UserModel reciever;

    @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_feedback_post,container,false);
            rating_bar = view.findViewById(R.id.rating_bar);
            default_feedback = view.findViewById(R.id.text_feedback);
            user_feedback = view.findViewById(R.id.edittext_feedback);
            btn_submit = view.findViewById(R.id.submit_button);
            uploadAuth = FirebaseAuth.getInstance();

            ratingdata = new RatingModel();
            reciever = new UserModel();


            rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                    rate = (int)rating;
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
                    Toast.makeText(getContext(), "need to post data from here to DB", Toast.LENGTH_SHORT).show();
                    posting_data();
                    passing_data();
                }
            });

            return view;
        }
    private void posting_data() {

        String myCurrentDateTime = DateFormat.getDateTimeInstance()
                .format(Calendar.getInstance().getTime());
        String Ad_id = uploadAuth.getCurrentUser().getUid();
        ratingdata.setCurrentDateTime(myCurrentDateTime);
        ratingdata.setRating(rate);
        ratingdata.setBuyerid(uploadAuth.getCurrentUser().getUid());
        ratingdata.setSellerid(reciever.getuserId());
        ratingdata.setFeedback(user_feedback.getText().toString());
        ratingdata.setImageUri("idhr image ka url dena hy");

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("UserRating")
                .child(Ad_id)
                .child(myCurrentDateTime);
        databaseReference.setValue(ratingdata);
    }

    private void passing_data(){
        Feedback_updateFragment feedback_updateFragment = new Feedback_updateFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String old_user_feedback = user_feedback.getText().toString();
        String old_feedback_comments = default_feedback.getText().toString();
        float old_user_stars=(float)rate;
        Bundle bundle = new Bundle();
        bundle.putFloat("rating", old_user_stars);
        bundle.putString("feedback", old_user_feedback);
        bundle.putString("comments", old_feedback_comments);
        feedback_updateFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, feedback_updateFragment);
        fragmentTransaction.commit();
    }

    }
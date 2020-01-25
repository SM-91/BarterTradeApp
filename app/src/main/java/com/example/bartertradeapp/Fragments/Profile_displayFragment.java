package com.example.bartertradeapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bartertradeapp.DataModels.RatingModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.R;
import com.example.bartertradeapp.adapters.user_feedback_adapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Profile_displayFragment extends BaseFragment implements user_feedback_adapter.ItemClickListener {

    Button update_profile;
    ImageView image_profile;
    TextView username, avg_feedback;
    RecyclerView feedback_recycler;
    LinearLayoutManager layoutManager;
    private user_feedback_adapter adapter;
    float avg_rating;
    String avg_rating_string;
    RatingBar avg_rating_bar;


    List<RatingModel> feedback_list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_display, container, false);

        feedback_list = new ArrayList<>();
        update_profile = view.findViewById(R.id.btn_update_profile);
        image_profile = view.findViewById(R.id.profile_image_view);
        username = view.findViewById(R.id.textview_username);
        avg_feedback = view.findViewById(R.id.textview_avg_rating);
        avg_rating_bar = view.findViewById(R.id.avg_rating_bar);
        feedback_recycler = view.findViewById(R.id.recyclerView_user_feedbacks);



        avg_rating_bar.setIsIndicator(true);
        avg_rating_bar.setClickable(false);

        uploadAuth = FirebaseAuth.getInstance();

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).addToBackStack(null).commit();
            }
        });


        // Recycler View Setup
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        feedback_recycler.setLayoutManager(layoutManager);
        adapter = new user_feedback_adapter(getContext(), feedback_list);
        adapter.setClickListener(this);

        //  Showing User Feedback
        String user_id = uploadAuth.getCurrentUser().getUid();
        viewDatabaseReference = FirebaseDatabase.getInstance().getReference("UserFeedback").child(user_id);
        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                feedback_list.clear();
                int temp_rating=0;
                int count =0;
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    RatingModel feedback = usersSnapshot.getValue(RatingModel.class);
                    feedback_list.add(feedback);
                    temp_rating = temp_rating + feedback.getRating();
                    count ++;
                }

                /*Bug fix here*/
                if(avg_rating_string != null){

                    avg_rating = Float.valueOf(temp_rating)/count;
                    avg_rating_string = String.valueOf(avg_rating);
                    Toast.makeText(getContext(), "Rate:" + avg_rating_string, Toast.LENGTH_SHORT).show();

                }
                // setting adapter to recycler View
                //recyclerView.setAdapter(adapter);
                feedback_recycler.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Setting User Profile VIEW.
        viewDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uploadAuth.getUid());
        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                username.setText(userModel.getUserName());
                avg_feedback.setText("Average Score "+avg_rating_string);
                avg_rating_bar.setRating(avg_rating);
                Picasso.get().load(userModel.getUserImageUrl())
                        .fit()
                        .into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        //Toast.makeText(getContext(), "Pos:"+position, Toast.LENGTH_SHORT).show();
    }
}

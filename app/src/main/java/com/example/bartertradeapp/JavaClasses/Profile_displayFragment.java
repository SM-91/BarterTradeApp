package com.example.bartertradeapp.JavaClasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
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
        feedback_recycler = view.findViewById(R.id.recyclerView_user_feedbacks);


        uploadAuth = FirebaseAuth.getInstance();
        // setting text
        //username.setText("Talal");
        avg_feedback.setText("4.5");


//        //profile pic setting
//        Picasso.get().load("yaha image ka path dena hy")
//                .fit()
//                .centerCrop()
//                .into(image_profile);
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

        // Path dena hy abi
        String Ad_id = uploadAuth.getCurrentUser().getUid();

        viewDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child("UserRating").child(Ad_id);
        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                feedback_list.clear();
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    RatingModel feedback = usersSnapshot.getValue(RatingModel.class);
                    feedback_list.add(feedback);
                }
                // setting adapter to recycler View
                //recyclerView.setAdapter(adapter);
                feedback_recycler.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child("UserDetails");
        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = usersSnapshot.getValue(UserModel.class);
                    username.setText(userModel.getUserName());
                    Picasso.get().load(userModel.getUserImageUrl())
                            .fit()
                            .centerCrop()
                            .into(image_profile);
                }
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

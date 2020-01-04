package com.example.bartertradeapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.JavaClasses.ChatFragment;
import com.example.bartertradeapp.JavaClasses.HomeFragment;
import com.example.bartertradeapp.JavaClasses.ProfileFragment;
import com.example.bartertradeapp.JavaClasses.UserAdsFragment;
import com.example.bartertradeapp.JavaClasses.UserUploadFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;

    UserModel userModel;

    TextView nav_header_user_name;
    TextView nav_header_user_email;
    ImageView img1;

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference reference = firebaseDatabase.getReference().child(firebaseAuth.getUid()).child("Profile");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userModel = new UserModel();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header_view = navigationView.getHeaderView(0);
        nav_header_user_name = (TextView) header_view.findViewById(R.id.nav_header_userName);
        nav_header_user_email = (TextView) header_view.findViewById(R.id.nav_header_userEmail);
        img1 = (ImageView) header_view.findViewById(R.id.nav_header_userProfilePic);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    UserModel userModel = userSnapshot.getValue(UserModel.class);
                    nav_header_user_name.setText(userModel.getUserName());
                    nav_header_user_email.setText(userModel.getUserEmail());
                    Picasso.get().load(userModel.getUserImageUrl())
                            .fit()
                            .centerCrop()
                            .into(img1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer , toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        /*Default Fragment*/
        if(savedInstanceState == null){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit();
        navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public void onBackPressed() {
        if(mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_add_new_product:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserUploadFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_myAds:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserAdsFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_signOut:
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserUploadFragment()).addToBackStack(null).commit();
                firebaseAuth.signOut();
                break;


        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }


}

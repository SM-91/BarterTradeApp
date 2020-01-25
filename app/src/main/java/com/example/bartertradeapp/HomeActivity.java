package com.example.bartertradeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.Fragments.Feedback_postFragment;
import com.example.bartertradeapp.Fragments.HomeFragment;
import com.example.bartertradeapp.Fragments.HomeFragmentExtend;
import com.example.bartertradeapp.Fragments.MapFragment;
import com.example.bartertradeapp.Fragments.MessageListActivity;
import com.example.bartertradeapp.Fragments.Profile_displayFragment;
import com.example.bartertradeapp.Fragments.UserAdsFragment;
import com.example.bartertradeapp.Fragments.UserUploadFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    //BottomNavigationView nav_bar;

    UserModel userModel;

    TextView nav_header_user_name;
    TextView nav_header_user_email;
    ImageView img1;

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        changeStatusBarColor();
        userModel = new UserModel();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*Navigation Drawer Header*/
        View header_view = navigationView.getHeaderView(0);
        nav_header_user_name = header_view.findViewById(R.id.nav_header_userName);
        nav_header_user_email = header_view.findViewById(R.id.nav_header_userEmail);
        img1 = header_view.findViewById(R.id.nav_header_userProfilePic);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Profile_displayFragment());
                mDrawer.closeDrawer(GravityCompat.START);
            }
        });
        changeStatusBarColor();

        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    nav_header_user_name.setText(userModel.getUserName());
                    nav_header_user_email.setText(userModel.getUserEmail());
                    Picasso.get().load(userModel.getUserImageUrl())
                            .fit()
                            .into(img1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*Bottom Navigation Bar*/
        BottomNavigationView nav_bar = findViewById(R.id.nav_bar);
        nav_bar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.grid:
                        loadFragment(new HomeFragment());
                        return true;

                    case R.id.list:
                        loadFragment(new HomeFragmentExtend());
                        return true;

                    case R.id.add:
                        loadFragment(new UserUploadFragment());
                        return true;

                    case R.id.map:
                        loadFragment(new Feedback_postFragment());
                        return true;

                    case R.id.profile_edit:
                        loadFragment(new Profile_displayFragment());
                        return true;
                    // ///// ADD more cases for different navigation bar options////////
                    default:
                        return false;
                }
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

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
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
                loadFragment(new HomeFragment());
                break;

            case R.id.nav_profile:
                loadFragment(new Profile_displayFragment());
                break;

            case R.id.nav_chat:
                Intent intent = new Intent(HomeActivity.this, MessageListActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_add_new_product:
                Intent requestIntent = new Intent(HomeActivity.this, RequestListActivity.class);
                startActivity(requestIntent);
                break;

            case R.id.nav_myAds:
                loadFragment(new UserAdsFragment());
                break;

            case R.id.nav_maps:
                loadFragment(new MapFragment());
                break;


            case R.id.nav_signOut:
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserUploadFragment()).addToBackStack(null).commit();
                firebaseAuth.signOut();
                finish();
                intent = new Intent(HomeActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;

        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

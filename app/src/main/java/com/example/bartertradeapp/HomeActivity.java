package com.example.bartertradeapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.JavaClasses.AddProductFragment;
import com.example.bartertradeapp.JavaClasses.BaseFragment;
import com.example.bartertradeapp.JavaClasses.ChatFragment;
import com.example.bartertradeapp.JavaClasses.Feedback_postFragment;
import com.example.bartertradeapp.JavaClasses.HomeFragment;
import com.example.bartertradeapp.JavaClasses.HomeFragmentExtend;
import com.example.bartertradeapp.JavaClasses.MapFragment;
import com.example.bartertradeapp.JavaClasses.MessageListActivity;
import com.example.bartertradeapp.JavaClasses.Profile_displayFragment;
import com.example.bartertradeapp.JavaClasses.UserAdsFragment;
import com.example.bartertradeapp.JavaClasses.UserUploadFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import androidx.fragment.app.Fragment;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    //BottomNavigationView nav_bar;

    UserModel userModel;

    TextView nav_header_user_name;
    TextView nav_header_user_email;
    ImageView img1;

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference reference = firebaseDatabase.getReference("Users").child("UserDetails");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        changeStatusBarColor();
        userModel = new UserModel();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        /*btn_testing = findViewById(R.id.btn_testing);
        btn_testing2 = findViewById(R.id.btn_testing2);*/

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*Navigation Drawer Header*/
        View header_view = navigationView.getHeaderView(0);
        nav_header_user_name = (TextView) header_view.findViewById(R.id.nav_header_userName);
        nav_header_user_email = (TextView) header_view.findViewById(R.id.nav_header_userEmail);
        img1 = (ImageView) header_view.findViewById(R.id.nav_header_userProfilePic);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Profile_displayFragment());
                mDrawer.closeDrawer(GravityCompat.START);
            }
        });
        changeStatusBarColor();
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
                        loadFragment(new AddProductFragment());
                        return true;

                    case R.id.map:
                        loadFragment(new ChatFragment());
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
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment()).addToBackStack(null).commit();
                break;

            case R.id.nav_add_new_product:
                loadFragment(new UserUploadFragment());
                break;

            case R.id.nav_myAds:
                loadFragment(new UserAdsFragment());
                break;

            case R.id.nav_maps:
                loadFragment(new Feedback_postFragment());
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

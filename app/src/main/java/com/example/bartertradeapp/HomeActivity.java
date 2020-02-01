package com.example.bartertradeapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bartertradeapp.DataModels.RatingModel;
import com.example.bartertradeapp.DataModels.RequestModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.Fragments.HomeFragment;
import com.example.bartertradeapp.Fragments.HomeFragmentExtend;
import com.example.bartertradeapp.Fragments.MessageFragment;
import com.example.bartertradeapp.Fragments.MessageListFragment;
import com.example.bartertradeapp.Fragments.Profile_displayFragment;
import com.example.bartertradeapp.Fragments.RequestListFragment;
import com.example.bartertradeapp.Fragments.UserAdsFragment;
import com.example.bartertradeapp.Fragments.UserUploadFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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



    TextView nav_header_user_name;
    TextView nav_header_user_email;
    ImageView img1;
    Intent intent;

    public static boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    public static LatLng curr;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = HomeActivity.class.getSimpleName( );
    private NavigationView navigationView;

    public static float avg_rating;
    public static String ad_id,msg_product_name,msg_category,avg_rating_string,msg_id;
    public static UserModel userModel,msg_temp;
    MessageFragment messageFragment;

    String uid = FirebaseAuth.getInstance().getUid();


    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        changeStatusBarColor();
        userModel = new UserModel();
        messageFragment = new MessageFragment();

        getLocationPermission();
        getDeviceLocation();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );
        mDrawer = findViewById(R.id.drawer_layout);



        //checkForNewRequest();

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
        Average_score();

        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance( ).getReference( "Users" ).child( uid );
        reference.addValueEventListener( new ValueEventListener( ) {

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
                switch (menuItem.getItemId()) {
                    case R.id.grid:
                        loadFragment(new HomeFragment());
                        return true;

                    case R.id.list:
                        loadFragment(new HomeFragmentExtend());
                        return true;

                    case R.id.add:
                        loadFragment(new UserUploadFragment());
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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        /*Default Fragment*/
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (getIntent().getExtras() != null) {
            String frag = intent.getExtras().getString("frag");
            switch (frag) {
                case "abc":

                    msg_id = intent.getExtras().getString("ad_id");
                    msg_product_name = intent.getExtras().getString("product_name");
                    msg_category = intent.getExtras().getString("category");
                    msg_temp = intent.getExtras().getParcelable("user");

//                    Bundle message = new Bundle();
//                    message.putString("ad_id", ad_id);
//                    message.putString("product_name", product_name);
//                    message.putString("category", category);
//                    message.putParcelable("user", temp);
//                    messageFragment.setArguments(message);
                    loadFragment(new MessageFragment());
                    break;
            }
        }

    }

    private void checkForNewRequest() {
        DatabaseReference requestReference = FirebaseDatabase.getInstance().getReference("Requests");
        requestReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                outerLoop:
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    for(DataSnapshot childOfChildSnapShot: childSnapshot.getChildren()) {
                        RequestModel requestModel = childOfChildSnapShot.getValue(RequestModel.class);

                        if(uid.equals(requestModel.getReciever().getuserId())) {
                            if(!requestModel.isAccepted()) {
                                Menu menu = navigationView.getMenu();
                                MenuItem navRequests = menu.findItem(R.id.nav_requests);
                                navRequests.setIcon(R.drawable.ic_chat_unread);
                                break outerLoop;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadFragment(Fragment fragment) {
        //getSupportFragmentManager( ).beginTransaction( ).replace( R.id.fragment_container, fragment ).addToBackStack( null ).commit( );

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        //Fragment_My_Clients newFragment = Fragment_My_Clients.newInstance();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.nav_home:
                loadFragment(new HomeFragment());
                break;

            case R.id.nav_profile:
                loadFragment(new Profile_displayFragment());

                break;

            case R.id.nav_chat:
//                intent = new Intent(HomeActivity.this, MessageListActivity.class);
//                startActivity(intent);

                loadFragment(new MessageListFragment());

                break;

            case R.id.nav_requests:
                loadFragment(new RequestListFragment());
//                Intent requestIntent = new Intent(HomeActivity.this, RequestListActivity.class);
//                startActivity(requestIntent);
                break;

            case R.id.nav_myAds:
                loadFragment(new UserAdsFragment());
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

    public void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            curr = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            Log.e(TAG, "current1" + curr);

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void Average_score() {
        //  Calculating Avg User Feedback

        String user_id = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference viewDatabaseReference = FirebaseDatabase.getInstance().getReference("UserFeedback").child(user_id);
        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int temp_rating = 0;
                int count = 0;
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    RatingModel feedback = usersSnapshot.getValue(RatingModel.class);
                    temp_rating = temp_rating + feedback.getRating();
                    count++;
                }
                /*Bug fix here*/
                avg_rating = Float.valueOf(temp_rating) / count;
                avg_rating_string = String.valueOf(avg_rating);
                //Toast.makeText(HomeActivity.this, "Rate:" + avg_rating_string, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

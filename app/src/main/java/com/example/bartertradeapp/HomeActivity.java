package com.example.bartertradeapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.bartertradeapp.DataModels.RatingModel;
import com.example.bartertradeapp.DataModels.RequestModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.Fragments.HomeFragment;
import com.example.bartertradeapp.Fragments.HomeFragmentExtend;
import com.example.bartertradeapp.Fragments.Profile_displayFragment;
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
    public static boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    UserModel userModel;

    TextView nav_header_user_name;
    TextView nav_header_user_email;
    ImageView img1;

    ProgressDialog progress;


    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    public static LatLng curr;
    private static final String TAG = HomeActivity.class.getSimpleName( );
    private NavigationView navigationView;

    public static float avg_rating;
    public static String avg_rating_string;


    String uid = FirebaseAuth.getInstance().getUid();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance( );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        changeStatusBarColor();
        hideKeyboard(this);
        Average_score();
        userModel = new UserModel( );

        getLocationPermission();
        getDeviceLocation( );

        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        mDrawer = findViewById( R.id.drawer_layout );

        navigationView = findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        //checkForNewRequest();

        /*Navigation Drawer Header*/
        View header_view = navigationView.getHeaderView( 0 );
        nav_header_user_name = header_view.findViewById( R.id.nav_header_userName );
        nav_header_user_email = header_view.findViewById( R.id.nav_header_userEmail );
        img1 = header_view.findViewById( R.id.nav_header_userProfilePic );
        img1.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                loadFragment( new Profile_displayFragment( ) );
                mDrawer.closeDrawer( GravityCompat.START );
            }
        } );
        changeStatusBarColor( );

        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance( ).getReference( "Users" ).child( uid );
        reference.addValueEventListener( new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue( UserModel.class );
                nav_header_user_name.setText( userModel.getUserName( ) );
                nav_header_user_email.setText( userModel.getUserEmail( ) );
                Picasso.get( ).load( userModel.getUserImageUrl( ) )
                        .fit( )
                        .into( img1 );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        /*Bottom Navigation Bar*/
        BottomNavigationView nav_bar = findViewById( R.id.nav_bar );
        nav_bar.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener( ) {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId( )) {
                    case R.id.grid:
                        loadFragment( new HomeFragment( ) );
                        return true;

                    case R.id.list:
                        loadFragment( new HomeFragmentExtend( ) );
                        return true;

                    case R.id.add:
                        loadFragment( new UserUploadFragment( ) );
                        return true;

                    case R.id.profile_edit:
                        loadFragment( new Profile_displayFragment( ) );
                        return true;
                    // ///// ADD more cases for different navigation bar options////////
                    default:
                        return false;
                }
            }
        } );

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close );

        mDrawer.addDrawerListener( toggle );
        toggle.syncState( );

        /*Default Fragment*/
        if (savedInstanceState == null) {
            getSupportFragmentManager( ).beginTransaction( ).replace( R.id.fragment_container, new HomeFragment( ) ).commit( );
            navigationView.setCheckedItem( R.id.nav_home );
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
        getSupportFragmentManager( ).beginTransaction( ).replace( R.id.fragment_container, fragment ).addToBackStack(null).commit( );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mDrawer.isDrawerOpen( GravityCompat.START )) {
            mDrawer.closeDrawer( GravityCompat.START );
        } else {
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId( )) {

            case R.id.nav_home:
                loadFragment( new HomeFragment( ) );
                break;

            case R.id.nav_profile:
                loadFragment( new Profile_displayFragment( ) );
                break;

            case R.id.nav_chat:
                Intent intent = new Intent( HomeActivity.this, MessageListActivity.class );
                startActivity( intent );
                break;

            case R.id.nav_requests:
                Intent requestIntent = new Intent(HomeActivity.this, RequestListActivity.class);
                startActivity(requestIntent);
                break;

            case R.id.nav_myAds:
                loadFragment( new UserAdsFragment( ) );
                break;

            case R.id.nav_signOut:
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserUploadFragment()).addToBackStack(null).commit();
                firebaseAuth.signOut( );
                finish( );
                intent = new Intent( HomeActivity.this, SignUpActivity.class );
                startActivity( intent );
                break;

        }

        mDrawer.closeDrawer( GravityCompat.START );
        return true;
    }

    public void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission( this.getApplicationContext( ),
                android.Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {

            ActivityCompat.requestPermissions( this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION );
        }
    }

    public void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation( );
                locationResult.addOnCompleteListener( this, new OnCompleteListener<Location>( ) {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful( )) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult( );
                            curr = new LatLng( mLastKnownLocation.getLatitude( ), mLastKnownLocation.getLongitude( ) );
                            Log.e( TAG, "current1" + curr );

                        } else {
                            Log.d( TAG, "Current location is null. Using defaults." );
                            Log.e( TAG, "Exception: %s", task.getException( ) );
                        }
                    }
                } );
            }
        } catch (SecurityException e) {
            Log.e( "Exception: %s", e.getMessage( ) );
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
                //Bug fix here//
                        avg_rating = Float.valueOf(temp_rating) / count;
                avg_rating_string = String.valueOf(avg_rating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}

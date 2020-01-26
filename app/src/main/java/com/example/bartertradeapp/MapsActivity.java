package com.example.bartertradeapp;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bartertradeapp.DataModels.ClusterMarker;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.Renderer.MyClusterManagerRenderer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;

import java.sql.Array;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    private static final String TAG = MapsActivity.class.getSimpleName( );
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private static final int DEFAULT_ZOOM = 20;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private double latitude,longitude;
    private String title;
    private String ad_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable( KEY_LOCATION );
            mCameraPosition = savedInstanceState.getParcelable( KEY_CAMERA_POSITION );
        }

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            ad_id = bundle.getString("ad_id");
        }


        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager( )
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );

    }
    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable( KEY_CAMERA_POSITION, mMap.getCameraPosition( ) );
            outState.putParcelable( KEY_LOCATION, mLastKnownLocation );
            super.onSaveInstanceState( outState );
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;


        //getuser( );
        getUserLatLng();


        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter( new GoogleMap.InfoWindowAdapter( ) {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater( ).inflate( R.layout.custom_info_contents,
                        (FrameLayout) findViewById( R.id.map ), false );

                TextView title = ((TextView) infoWindow.findViewById( R.id.title ));
                title.setText( marker.getTitle( ) );

                TextView snippet = ((TextView) infoWindow.findViewById( R.id.snippet ));
                snippet.setText( marker.getSnippet( ) );

                return infoWindow;
            }
        } );



        // Prompt the user for permission.
        getLocationPermission( );

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI( );


        // Get the current location of the device and set the position of the map.
        //getDeviceLocation( );
    }

    private void getUserLatLng(){
        DatabaseReference userReference;
        userReference = FirebaseDatabase.getInstance().getReference("ProductsAndServices").child( ad_id );
        userReference.addValueEventListener( new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserUploadProductModel userUploadProductModel = dataSnapshot.getValue(UserUploadProductModel.class);
                latitude = userUploadProductModel.getLatitude();
                longitude = userUploadProductModel.getLongitude();
                title = userUploadProductModel.getProductName();

                PointerPlacer(latitude,longitude,title);
                /*   for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    private void PointerPlacer(double latitude , double longitude, String title){
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .icon(BitmapDescriptorFactory
                        .defaultMarker( BitmapDescriptorFactory.HUE_GREEN)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitude, longitude), DEFAULT_ZOOM));
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

    public void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled( true );
                mMap.getUiSettings( ).setMyLocationButtonEnabled( true );
            } else {
                mMap.setMyLocationEnabled( false );
                mMap.getUiSettings( ).setMyLocationButtonEnabled( false );
                mLastKnownLocation = null;
                getLocationPermission( );
            }
        } catch (SecurityException e) {
            Log.e( "Exception: %s", e.getMessage( ) );
        }
    }


}
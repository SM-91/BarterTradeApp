package com.example.bartertradeapp.JavaClasses;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Address;

import com.example.bartertradeapp.adapters.CustomInfoWindowAdapter;
import com.google.android.gms.maps.PlaceInfo;
import com.google.android.gms.maps.model.LatLng;
import android.location.Location;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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


import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static MapFragment INSTANCE = null;

    private String uid;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference , getUserRef;

    private UserUploadProductModel userUploadProductModel;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private Marker mMarker;
    private PlaceInfo mPlace;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SeekBar seekBar;
    private int progress;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    private GeoPoint geo;
    private CircleOptions circleOptions =new CircleOptions();
    private Circle circle;

    private FirebaseFirestore mDb;
    private ListenerRegistration mChatMessageEventListener, mUserListEventListener;


    private static ArrayList<UserUploadProductModel> userUploadProductModelsList = new ArrayList<>();

    private LatLng diff;
    private LatLng curr;
    private static ArrayList<Double> distances=new ArrayList<>();
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();

    SupportMapFragment mMapFragment;


    private MapView mapView;
    private GoogleMap map;


    public MapFragment(){
    }

   /* public static MapFragment getInstance(){
        if (INSTANCE == null)
            INSTANCE = new MapFragment();
        return INSTANCE;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//Make sure you have this line of code.

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map,container,false);

        userUploadProductModel = new UserUploadProductModel();
        seekBar = (SeekBar)view.findViewById(R.id.seekBar);

        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.current_places_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showCurrentPlace();
            writeNewUser(geo);
            UserUploadFragment useruplaod = new UserUploadFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, useruplaod);
            fragmentTransaction.addToBackStack(null).commit();;
        }
        return true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    /*@Override
    public void onMapReady(GoogleMap map) {

    }*/


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.nav_maps);
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);


        mGeoDataClient = Places.getGeoDataClient(getContext(), null);


        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext(), null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        // Build the map.
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.nav_maps);
        //mMapFragment.getMapAsync(this);


        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        /*if(mapView != null){

        }*/
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        getuser();




        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {



            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }



            @Override
            public View getInfoContents(Marker marker) {

                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents, null);
                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());
                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });


        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(0,0))
                .radius(100)
                .strokeWidth(5f)
                .fillColor(0x515000FF));


        // Get the current location of the device and set the position of the map.
        getDeviceLocation();





    }




    //Get Address from Latitude and longitude//



    public String getAddress(Context ctx, double lat, double lng){

        String fullAdd = null;
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat,lng,1);
            if (addresses.size()>0){
                Address address = addresses.get(0);
                fullAdd = address.getAddressLine(0);
            }
        }catch(IOException ex){
            ex.printStackTrace();

        }
        return fullAdd;


    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            Log.i("Last Location", String.valueOf(mLastKnownLocation));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            geo= new GeoPoint((float)mLastKnownLocation.getLatitude(),(float)mLastKnownLocation.getLongitude());
                            curr=new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                            //writeNewUser(geo);
                            circle.setCenter(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()));
                            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override

                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    progress = progress*1000;
                                    Log.e(TAG, "onProgressChanged: "+ progress);
                                    circle.setRadius(progress);

                                    Log.e(TAG,"user loc size= "+ userUploadProductModelsList.size());
                                    for (int i = 0; i < userUploadProductModelsList.size(); i++){

                                        diff=new LatLng(userUploadProductModelsList.get(i).getLatitude(),userUploadProductModelsList.get(i).getLongitude());
                                        distances.add(distanceBetween(curr,diff));

                                    }
                                    Log.e(TAG,"distances size= "+userUploadProductModelsList.size());

                                    PointerPlacer(distances, progress ,userUploadProductModelsList);
                                }
                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        }
        catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void PointerPlacer(ArrayList<Double> distances, int progress, ArrayList<UserUploadProductModel> userUploadProductModelsList) {
        removeTripMarkers();
        if (progress==1000) {
            for (int i = 0; i < userUploadProductModelsList.size(); i++){
                if (distances.get(i)<1000 && distances.get(i)>10){
                    mTripMarkers.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(userUploadProductModelsList.get(i).getLatitude(), userUploadProductModelsList.get(i).getLongitude()))
                            .title("Hello world")));
                    //   return;
                }
                Log.e(TAG, "PointerPlacer: here5" );
            }
        }
        else if (progress==2000) {
            for (int i = 0; i < userUploadProductModelsList.size(); i++){
                if (distances.get(i)<2000 && distances.get(i)>10){
                    mTripMarkers.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(userUploadProductModelsList.get(i).getLatitude(), userUploadProductModelsList.get(i).getLongitude()))
                            .title("Hello world")));
                    //   return;

                }
                Log.e(TAG, "PointerPlacer: here5" );
            }

        }
        else if (progress==3000) {
            for (int i = 0; i < userUploadProductModelsList.size(); i++){
                if (distances.get(i)<3000 && distances.get(i)>10){
                    mTripMarkers.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(userUploadProductModelsList.get(i).getLatitude(), userUploadProductModelsList.get(i).getLongitude()))
                            .title("Hello world")));
                    //   return;
                }
                Log.e(TAG, "PointerPlacer: here5" );
            }
        }
        else if (progress==4000) {
            for (int i = 0; i < userUploadProductModelsList.size(); i++){
                if (distances.get(i)<4000 && distances.get(i)>10){
                    mTripMarkers.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(userUploadProductModelsList.get(i).getLatitude(), userUploadProductModelsList.get(i).getLongitude()))
                            .title("Hello world")));
                    //   return;
                }
                Log.e(TAG, "PointerPlacer: here5" );
            }
        }
        else if (progress==5000) {
            for (int i = 0; i < userUploadProductModelsList.size(); i++){
                if (distances.get(i)<5000 && distances.get(i)>10){
                    mTripMarkers.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(userUploadProductModelsList.get(i).getLatitude(), userUploadProductModelsList.get(i).getLongitude()))
                            .title("Hello world")));
                    //   return;
                }
                Log.e(TAG, "PointerPlacer: here5" );
            }
        }
    }





    //playing with address
    private void moveCamera(LatLng latLng, float zoom, com.google.android.gms.maps.PlaceInfo placeInfo){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.clear();

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));

        if(placeInfo != null){
            try{
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n" +
                        "Price Rating: " + placeInfo.getRating() + "\n";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);
                mMarker = mMap.addMarker(options);

            }catch (NullPointerException e){
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage() );
            }
        }else{
            mMap.addMarker(new MarkerOptions().position(latLng));
        }

        //hideSoftKeyboard();
    }


    /**
     * adds new user location to firebase
     */
    private void writeNewUser(GeoPoint geo ) {

        userUploadProductModel.setLatitude(geo.getLatitude());
        userUploadProductModel.setLongitude(geo.getLongitude());

        if (firebaseAuth.getCurrentUser() != null) {
            // Name, email address, and profile photo Url
            uid = firebaseAuth.getCurrentUser().getUid();
            Toast.makeText(getContext(), uid, Toast.LENGTH_LONG).show();

        }

        databaseReference = FirebaseDatabase.getInstance().getReference("UserLocation").child(uid);
        //DatabaseReference dR = FirebaseDatabase.getInstance()
        databaseReference.setValue(userUploadProductModel);
        //mDatabase.child("User").child(uid).setValue(user1);
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.
                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }

                                int i = 0;
                                mLikelyPlaceNames = new String[count];
                                mLikelyPlaceAddresses = new String[count];
                                mLikelyPlaceAttributions = new String[count];
                                mLikelyPlaceLatLngs = new LatLng[count];

                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                            .getAddress();
                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                            .getAttributions();
                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                    i++;
                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                                // Show a dialog offering the user the list of likely places, and add a
                                // marker at the selected place.
                                openPlacesDialog();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        }
        else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }
    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private double distanceBetween(LatLng point1, LatLng point2) {

        if (point1 == null || point2 == null) {
            //return null;
            return 0;
        }
        double distance= SphericalUtil.computeDistanceBetween(point1, point2);
        Log.e(TAG, "Distance "+distance);
        return distance;

        //return ;
    }
    private void getuser(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        getUserRef = firebaseDatabase.getReference("UserLocation");
        //user=new UserLocation();
        //adapter=new ArrayAdapter<UserLocation>(this,);
        getUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    userUploadProductModel= snapshot.getValue(UserUploadProductModel.class);
                    userUploadProductModelsList.add(userUploadProductModel);

                    Log.e(TAG,"UID "+userUploadProductModel.getLatitude());
                }
                Log.e(TAG,"size= "+userUploadProductModelsList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    /**
     * removes markers from maps
     */
    private void removeTripMarkers(){
        for(Marker marker: mTripMarkers){
            marker.remove();
        }
        return;
    }

}

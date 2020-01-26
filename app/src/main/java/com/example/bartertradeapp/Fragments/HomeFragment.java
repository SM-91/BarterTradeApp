package com.example.bartertradeapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bartertradeapp.DataModels.UserHistoryModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.DetailedActivity;
import com.example.bartertradeapp.R;
import com.example.bartertradeapp.adapters.custom_list_adapter;
import com.example.bartertradeapp.adapters.custom_nearest_adapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static com.example.bartertradeapp.HomeActivity.curr;

public class HomeFragment extends BaseFragment implements custom_list_adapter.ItemClickListener, custom_nearest_adapter.ItemClickListener {

    private RecyclerView recyclerView_latest, recyclerView_history, recyclerView_nearest;
    private custom_list_adapter adapter_history;
    private custom_nearest_adapter adapter_nearest, adapter_latest;
    LinearLayoutManager layoutManager_latest, layoutManager_history, layoutManager_nearest;

    ArrayList<String> category_list;

    FirebaseAuth uploadAuth;
    List<UserUploadProductModel> latest_ads, history_ads, nearest_ads;
    Date date;

    private String ad_id, category;

    //Location

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        uploadAuth = FirebaseAuth.getInstance();
        latest_ads = new ArrayList<>();
        history_ads = new ArrayList<>();
        nearest_ads = new ArrayList<>();

        category_list = new ArrayList<>();


        // set up the RecyclerView
        recyclerView_latest = view.findViewById(R.id.recyclerView_latest);
        recyclerView_history = view.findViewById(R.id.recyclerView_history);
        recyclerView_nearest = view.findViewById(R.id.recyclerView_nearest);

        //layoutManager = new LinearLayoutManager(getContext());
        layoutManager_latest = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager_history = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager_nearest = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView_latest.setLayoutManager(layoutManager_latest);
        recyclerView_history.setLayoutManager(layoutManager_history);
        recyclerView_nearest.setLayoutManager(layoutManager_nearest);

        // initializing adapter
        adapter_latest = new custom_nearest_adapter(getContext(), latest_ads);
        adapter_latest.setClickListener(this);

        adapter_history = new custom_list_adapter(getContext(), history_ads);
        //adapter_history.setClickListener(this);

        adapter_nearest = new custom_nearest_adapter(getContext(), nearest_ads);
        //adapter_nearest.setClickListener(this);


        // Setting data by History Data
        viewDatabaseReference = FirebaseDatabase.getInstance().getReference("UserHistory");
        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                category_list.clear();
                category_list.add("nothing");
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : usersSnapshot.getChildren()) {
                        UserHistoryModel history = ds.getValue(UserHistoryModel.class);
                        //category_list.add(history.getCategory());
                        category = history.getCategory();
                        category_search(category);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // taking Latest Ads according to time...
        latest_search();


        // Setting data by Location
        nearest_search();


        //Float distance = calculateDistance(49.9342639,11.5832404,49.434669,11.051320);
        //Toast.makeText(getContext(), "distance"+distance, Toast.LENGTH_SHORT).show();

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {

        intent = new Intent(getContext(), DetailedActivity.class);
        userUploadProductModel = adapter_latest.getItem(position);
        //userUploadProductModel = adapter_nearest.getItem(position);
        //userUploadProductModel = adapter_history.getItem(position);
        ad_id = userUploadProductModel.getAdId();
        Date myCurrentDateTime = userUploadProductModel.getCurrentDateTime();
        String myCurrentDateTimeString = userUploadProductModel.getCurrentDateTimeString();
        pname = userUploadProductModel.getProductName();
        pdesc = userUploadProductModel.getProductDescription();
        pexch = userUploadProductModel.getPossibleExchangeWith();
        pest = userUploadProductModel.getProductEstimatedMarketValue();
        ptype = userUploadProductModel.getProductType();
        pcategory = userUploadProductModel.getProductCategoryList();
        pcondition = userUploadProductModel.getProductCondition();
        String serviceName = userUploadProductModel.getServiceName();
        String serviceCategory = userUploadProductModel.getServiceCategory();
        String serviceDescription = userUploadProductModel.getServiceDescription();
        String serviceEstimatedMarketValue = userUploadProductModel.getServiceEstimatedMarketValue();
        String servicePossibleExchangeWith = userUploadProductModel.getServicePossibleExchangeWith();
        String serviceImageUri = userUploadProductModel.getServiceImageUri();
        String tag = userUploadProductModel.getTag();
        UserModel postedBy = userUploadProductModel.getPostedBy();
        ArrayList<String> pimagelist = userUploadProductModel.getmArrList();


        pimg = userUploadProductModel.getmImageUri();

        intent.putExtra("name", pname);
        intent.putExtra("desc", pdesc);
        intent.putExtra("ad_id", ad_id);
        intent.putExtra("exchange", pexch);
        intent.putExtra("est", pest);
        intent.putExtra("type", ptype);
        intent.putExtra("category", pcategory);
        intent.putExtra("condition", pcondition);
        intent.putExtra("exch", pexch);
        intent.putExtra("worth", pest);
        intent.putExtra("Key", myCurrentDateTime);
        intent.putExtra("myCurrentDateTimeString", myCurrentDateTimeString);
        intent.putExtra("serviceName", serviceName);
        intent.putExtra("serviceCategory", serviceCategory);
        intent.putExtra("serviceDescription", serviceDescription);
        intent.putExtra("serviceEstimatedMarketValue", serviceEstimatedMarketValue);
        intent.putExtra("servicePossibleExchangeWith", servicePossibleExchangeWith);
        intent.putExtra("serviceImageUri", serviceImageUri);
        intent.putExtra("tag", tag);
        intent.putExtra("user", postedBy);
        intent.putExtra("imagelist", pimagelist);
        intent.putExtra("image", pimg);
        startActivity(intent);
    }

    @Override
    public void onNearestItemClick(View view, int position) {

        intent = new Intent(getContext(), DetailedActivity.class);
        //userUploadProductModel = adapter_latest.getItem(position);
        userUploadProductModel = adapter_nearest.getItem(position);
        ad_id = userUploadProductModel.getAdId();
        Date myCurrentDateTime = userUploadProductModel.getCurrentDateTime();
        String myCurrentDateTimeString = userUploadProductModel.getCurrentDateTimeString();
        pname = userUploadProductModel.getProductName();
        pdesc = userUploadProductModel.getProductDescription();
        pexch = userUploadProductModel.getPossibleExchangeWith();
        pest = userUploadProductModel.getProductEstimatedMarketValue();
        ptype = userUploadProductModel.getProductType();
        pcategory = userUploadProductModel.getProductCategoryList();
        pcondition = userUploadProductModel.getProductCondition();
        String serviceName = userUploadProductModel.getServiceName();
        String serviceCategory = userUploadProductModel.getServiceCategory();
        String serviceDescription = userUploadProductModel.getServiceDescription();
        String serviceEstimatedMarketValue = userUploadProductModel.getServiceEstimatedMarketValue();
        String servicePossibleExchangeWith = userUploadProductModel.getServicePossibleExchangeWith();
        String serviceImageUri = userUploadProductModel.getServiceImageUri();
        String tag = userUploadProductModel.getTag();
        UserModel postedBy = userUploadProductModel.getPostedBy();
        ArrayList<String> pimagelist = userUploadProductModel.getmArrList();


        pimg = userUploadProductModel.getmImageUri();

        intent.putExtra("name", pname);
        intent.putExtra("desc", pdesc);
        intent.putExtra("ad_id", ad_id);
        intent.putExtra("exchange", pexch);
        intent.putExtra("est", pest);
        intent.putExtra("type", ptype);
        intent.putExtra("category", pcategory);
        intent.putExtra("condition", pcondition);
        intent.putExtra("exch", pexch);
        intent.putExtra("worth", pest);
        intent.putExtra("Key", myCurrentDateTime);
        intent.putExtra("myCurrentDateTimeString", myCurrentDateTimeString);
        intent.putExtra("serviceName", serviceName);
        intent.putExtra("serviceCategory", serviceCategory);
        intent.putExtra("serviceDescription", serviceDescription);
        intent.putExtra("serviceEstimatedMarketValue", serviceEstimatedMarketValue);
        intent.putExtra("servicePossibleExchangeWith", servicePossibleExchangeWith);
        intent.putExtra("serviceImageUri", serviceImageUri);
        intent.putExtra("tag", tag);
        intent.putExtra("user", postedBy);
        intent.putExtra("imagelist", pimagelist);
        intent.putExtra("image", pimg);
        startActivity(intent);
    }

    public void latest_search() {
        date = Calendar.getInstance().getTime();
        //Date date =DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        viewDatabaseReference = FirebaseDatabase.getInstance().getReference("ProductsAndServices");
        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                latest_ads.clear();
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    UserUploadProductModel latest_search = usersSnapshot.getValue(UserUploadProductModel.class);

                    Date datestring = latest_search.getCurrentDateTime();
                    //Toast.makeText(getContext(), ""+datestring, Toast.LENGTH_SHORT).show();
                    Long hour;
                    hour = printDifference(datestring, date);
                    Toast.makeText(getContext(), "Hour:" + hour, Toast.LENGTH_SHORT).show();
                    if (hour < 24) {
                        latest_ads.add(latest_search);
                    }

                }
                recyclerView_latest.setAdapter(adapter_latest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void category_search(final String category) {
        viewDatabaseReference = FirebaseDatabase.getInstance().getReference("ProductsAndServices");
        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                history_ads.clear();
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    UserUploadProductModel latest_search = usersSnapshot.getValue(UserUploadProductModel.class);
                    if (Pattern.compile(Pattern.quote(category), Pattern.CASE_INSENSITIVE).matcher(latest_search.getProductCategoryList()).find()) {
                        history_ads.add(latest_search);
                    }
                }
                recyclerView_history.setAdapter(adapter_history);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void nearest_search() {
        viewDatabaseReference = FirebaseDatabase.getInstance().getReference("ProductsAndServices");
        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    UserUploadProductModel nearest = usersSnapshot.getValue(UserUploadProductModel.class);

                    float distance;
                    distance = calculateDistance(curr.latitude, curr.longitude, nearest.getLatitude(), nearest.getLongitude());
                    Toast.makeText(getContext(), "distance" + distance, Toast.LENGTH_SHORT).show();
                    // distance is in KM
                    if (distance < 3) {
                        nearest_ads.add(nearest);
                    }
                }
                recyclerView_nearest.setAdapter(adapter_nearest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static float calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        double meterConversion = 1.609;

        return new Float(dist * meterConversion).floatValue();
    }

    public long printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        return elapsedHours;
    }

}

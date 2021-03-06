package com.example.bartertradeapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bartertradeapp.DataModels.UserHistoryModel;
import com.example.bartertradeapp.DataModels.UserModel;
import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.example.bartertradeapp.DetailedActivity;
import com.example.bartertradeapp.R;
import com.example.bartertradeapp.adapters.CustomListAdapter;
import com.example.bartertradeapp.adapters.CustomNearestAdapter;
import com.example.bartertradeapp.adapters.CustomLatestAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static com.example.bartertradeapp.HomeActivity.curr;

public class HomeFragment extends BaseFragment implements CustomListAdapter.ItemClickListener, CustomNearestAdapter.ItemClickListener, CustomLatestAdapter.ItemClickListener {

    private RecyclerView recyclerView_latest, recyclerView_recommended, recyclerView_nearest;
    private CustomListAdapter adapter_recommended;
    private CustomLatestAdapter adapter_latest;
    private CustomNearestAdapter adapter_nearest;
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
        recyclerView_recommended = view.findViewById(R.id.recyclerView_history);
        recyclerView_nearest = view.findViewById(R.id.recyclerView_nearest);

        //layoutManager = new LinearLayoutManager(getContext());
        layoutManager_latest = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager_latest.setReverseLayout(true);
        layoutManager_latest.setStackFromEnd(true);
        layoutManager_history = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager_history.setReverseLayout(true);
        layoutManager_history.setStackFromEnd(true);
        layoutManager_nearest = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager_nearest.setReverseLayout(true);
        layoutManager_nearest.setStackFromEnd(true);

        recyclerView_latest.setLayoutManager(layoutManager_latest);
        recyclerView_recommended.setLayoutManager(layoutManager_history);
        recyclerView_nearest.setLayoutManager(layoutManager_nearest);

        // initializing adapter
        adapter_recommended = new CustomListAdapter(getContext(), history_ads);
        adapter_recommended.setClickListener(this);

        adapter_latest = new CustomLatestAdapter(getContext(), latest_ads);
        adapter_latest.setClickListener(this);

        adapter_nearest = new CustomNearestAdapter(getContext(), nearest_ads);
        adapter_nearest.setClickListener(this);




        getUserHistory();

        // taking Latest Ads according to time...
        latest_search();

        // Setting data by Location
        nearest_search();

        //Float distance = calculateDistance(49.9342639,11.5832404,49.434669,11.051320);
        //Toast.makeText(getContext(), "distance"+distance, Toast.LENGTH_SHORT).show();

        return view;
    }


    public void getUserHistory(){
        // Setting data by History Data
        DatabaseReference viewDatabaseReference;
        viewDatabaseReference = FirebaseDatabase.getInstance().getReference("UserHistory").child(uploadAuth.getUid());
        viewDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                category_list.clear();
                category_list.add("nothing");
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                        UserHistoryModel history = usersSnapshot.getValue(UserHistoryModel.class);
                        //category_list.add(history.getCategory());
                        category = history.getCategory();
                        category_search(category);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void latest_search() {
        date = Calendar.getInstance().getTime();
        //Date date =DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        DatabaseReference searchref;
        searchref = FirebaseDatabase.getInstance().getReference("ProductsAndServices");
        searchref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                latest_ads.clear();
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    UserUploadProductModel latest_search = usersSnapshot.getValue(UserUploadProductModel.class);

                    Date datestring = latest_search.getCurrentDateTime();
                    //Toast.makeText(getContext(), ""+datestring, Toast.LENGTH_SHORT).show();
                    Long hour;
                    hour = printDifference(datestring, date);
                    //Toast.makeText(getContext(), "Hour:" + hour, Toast.LENGTH_SHORT).show();
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
        DatabaseReference byCategorySearchRef;
        byCategorySearchRef = FirebaseDatabase.getInstance().getReference("ProductsAndServices");
        byCategorySearchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                history_ads.clear();
                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    UserUploadProductModel latest_search = usersSnapshot.getValue(UserUploadProductModel.class);
                    try{
                        if (Pattern.compile(Pattern.quote(category), Pattern.CASE_INSENSITIVE).matcher(latest_search.getProductCategoryList()).find()) {
                            history_ads.add(latest_search);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }


                }
                recyclerView_recommended.setAdapter(adapter_recommended);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void nearest_search() {
        DatabaseReference byNearestSearchRef;
        byNearestSearchRef = FirebaseDatabase.getInstance().getReference("ProductsAndServices");
        byNearestSearchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                    UserUploadProductModel nearest = usersSnapshot.getValue(UserUploadProductModel.class);

                    try{
                        float distance;
                        distance = calculateDistance(curr.latitude, curr.longitude, nearest.getLatitude(), nearest.getLongitude());
                        //Toast.makeText(getContext(), "distance" + distance, Toast.LENGTH_SHORT).show();
                        // distance is in KM
                        if (distance < 3) {
                            nearest_ads.add(nearest);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
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

    @Override
    public void onItemClick(View view, int position) {

        intent = new Intent(getContext(), DetailedActivity.class);
        userUploadProductModel = adapter_recommended.getItem(position);
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

    @Override
    public void onLatestItemClick(View view, int position) {

        intent = new Intent(getContext(), DetailedActivity.class);
        //userUploadProductModel = adapter_latest.getItem(position);
        userUploadProductModel = adapter_latest.getItem(position);
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


}

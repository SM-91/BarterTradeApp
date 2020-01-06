package com.example.bartertradeapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.bartertradeapp.JavaClasses.ViewPageAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailedActivity extends AppCompatActivity {

    TextView textView_name;
    TextView textView_desc;
    TextView textView_exch;
    TextView textView_est;
    TextView textView_type;
    TextView textView_category;
    TextView textView_condition;
    ImageView imageView;
    ViewPager viewPager;
    ViewPageAdapter adapter = null;

    private ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    private ArrayList<String> listimages = new ArrayList<String>() ;

    @Override
    protected void onStart() {
        super.onStart();
        textView_name = findViewById(R.id.textview_ad_name);
        textView_desc = findViewById(R.id.textview_ad_desc);
        textView_exch = findViewById(R.id.textview_ad_exchange);
        textView_est = findViewById(R.id.textview_ad_estimated);
        textView_type = findViewById(R.id.textview_ad_type);
        textView_category = findViewById(R.id.textview_ad_category);
        textView_condition = findViewById(R.id.textview_ad_condition);
        imageView = findViewById(R.id.image_display);

        /*ViewPager*/
        viewPager = findViewById(R.id.view_pager_image);
        adapter = new ViewPageAdapter(this, mArrayUri);
        viewPager.getOffscreenPageLimit();

        viewPager.setAdapter(adapter);


        Bundle bundle = getIntent().getExtras();
        String name = getIntent().getStringExtra("name");
        String desc = getIntent().getStringExtra("desc");
        String exch = getIntent().getStringExtra("exchange");
        String est = getIntent().getStringExtra("est");
        String type = getIntent().getStringExtra("type");
        String category = getIntent().getStringExtra("category");
        String condition = getIntent().getStringExtra("condition");
        String mimage = getIntent().getStringExtra("image");
        listimages = getIntent().getStringArrayListExtra("imagelist");

        if (bundle!= null) {

            if(mimage != null){
                imageView.setVisibility(View.VISIBLE);
                Picasso.get().load(mimage)
                        .fit()
                        .centerCrop()
                        .into(imageView);
            }
            else if (!listimages.isEmpty()){
                viewPager.setVisibility(View.VISIBLE);
                mArrayUri.clear();
                for (int i=0 ; i <listimages.size() ; i++){
                    Uri tem_uri = Uri.parse(listimages.get(i));
                    mArrayUri.add(tem_uri);
                }
                adapter.notifyDataSetChanged();
            }
            textView_name.setText(name);
            textView_desc.setText(desc);
            textView_exch.setText("Exchange with: " + exch);
            textView_est.setText("Estimated Price: "+est);
            textView_type.setText(type);
            textView_category.setText(category);
            textView_condition.setText(condition);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

    }
}
package com.example.bartertradeapp;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bartertradeapp.DataModels.UserUploadProductModel;
import com.squareup.picasso.Picasso;

public class DetailedActivity extends AppCompatActivity {

    TextView textView_name;
    TextView textView_desc;
    TextView textView_exch;
    TextView textView_est;
    TextView textView_type;
    TextView textView_category;
    TextView textView_condition;
    ImageView imageView;

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

        Bundle bundle = getIntent().getExtras();
        String name = getIntent().getStringExtra("name");
        String desc = getIntent().getStringExtra("desc");
        String exch = getIntent().getStringExtra("exchange");
        String est = getIntent().getStringExtra("est");
        String type = getIntent().getStringExtra("type");
        String category = getIntent().getStringExtra("category");
        String condition = getIntent().getStringExtra("condition");
        String mimage = getIntent().getStringExtra("image");
        if (bundle!= null) {
            //Toast.makeText(this, ""+image, Toast.LENGTH_SHORT).show();
            Picasso.get().load(mimage)
                    .fit()
                    .centerCrop()
                    .into(imageView);
            textView_name.setText(name);
            textView_desc.setText(desc);
            textView_exch.setText("Exchange with: " + exch);
            textView_est.setText("Estimated Price: "+est);
            textView_type.setText(type);
            textView_category.setText(category);
            textView_condition.setText(condition);
            final UserUploadProductModel userUploadProductModel = new UserUploadProductModel();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);






    }
}
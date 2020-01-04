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
    TextView textView_worth;
    ImageView imageView;

    @Override
    protected void onStart() {
        super.onStart();
        textView_name = findViewById(R.id.name);
        textView_desc = findViewById(R.id.details);
        textView_exch = findViewById(R.id.exch);
        textView_worth = findViewById(R.id.worth);
        imageView = findViewById(R.id.image_display);

        Bundle bundle = getIntent().getExtras();
        String name = getIntent().getStringExtra("name");
        String desc = getIntent().getStringExtra("desc");
        String exch = getIntent().getStringExtra("exch");
        String worth = getIntent().getStringExtra("worth");
        String mimage = getIntent().getStringExtra("image");
        if (bundle!= null) {
            //Toast.makeText(this, ""+image, Toast.LENGTH_SHORT).show();
            Picasso.get().load(mimage)
                    .fit()
                    .centerCrop()
                    .into(imageView);
            textView_name.setText(name);
            textView_desc.setText(desc);
            textView_exch.setText(exch);
            textView_worth.setText(worth);
            final UserUploadProductModel userUploadProductModel = new UserUploadProductModel();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);






    }
}

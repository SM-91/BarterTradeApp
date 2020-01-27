package com.example.bartertradeapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewPageAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Uri> imageUrls;

    public ViewPageAdapter(Context context, ArrayList<Uri> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }


    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        Uri uri = imageUrls.get(position);
        ImageView imageView = new ImageView(context);
        Picasso.get()
                .load(uri)
                .resize(400,200)
                .into(imageView);

        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

package com.example.bartertradeapp.adapters;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.bartertradeapp.JavaClasses.MapFragment;

public class FragmentAdapter extends FragmentPagerAdapter {

    Context context;

    public FragmentAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;

    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return MapFragment.getInstance();
        else
            return null;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
            return "Map Fragment";
        }

        return "";
    }
}

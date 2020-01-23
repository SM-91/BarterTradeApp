package com.example.bartertradeapp.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.bartertradeapp.Fragments.AddProductFragment;
import com.example.bartertradeapp.Fragments.AddServiceFragment;

public class UploadTabsAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;

    public UploadTabsAdapter(Context context, FragmentManager fragmentManager, int totalTabs){
        super(fragmentManager);
        this.context = context;
        this.totalTabs = totalTabs;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AddProductFragment addProductFragment = new AddProductFragment();
                return addProductFragment;

            case 1:
                AddServiceFragment addServiceFragment = new AddServiceFragment();
                return addServiceFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}

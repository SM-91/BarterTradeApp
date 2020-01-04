package com.example.bartertradeapp.JavaClasses;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.bartertradeapp.R;
import com.google.android.material.tabs.TabLayout;

public class UserUploadFragment extends BaseFragment {

    TabLayout tabLayout;
    ViewPager uploadViewPager;
    Context context;

    FrameLayout frameLayout;
    Fragment fragment = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product,container,false);

        tabLayout=view.findViewById(R.id.tabLayout);
        uploadViewPager = view.findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Add Product"));
        tabLayout.addTab(tabLayout.newTab().setText("Add Service"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        /*frameLayout=view.findViewById(R.id.frameLayout);*/
        final UploadTabsAdapter adapter = new UploadTabsAdapter(this.context,getChildFragmentManager(),
                tabLayout.getTabCount());
        uploadViewPager.setAdapter(adapter);
        uploadViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        onTabLayoutListener();

     /*   fragment = new AddProductFragment() ;
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();*/

        return view;
    }

    private void onTabLayoutListener(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                uploadViewPager.setCurrentItem(tab.getPosition());

                /*FragmentManager fm = getChildFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frameLayout, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}

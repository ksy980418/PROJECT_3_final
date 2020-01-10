package com.example.project_3;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class PageAdapter extends FragmentPagerAdapter {

    private int numoftabs;
    private FragmentOne tab1;
    private FragmentTwo tab2;
    private FragmentThree tab3;

    public PageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numoftabs = numOfTabs;
        tab1 = new FragmentOne();
        tab2 = new FragmentTwo();
        tab3 = new FragmentThree();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return tab1;
            case 1:
                return tab2;
            case 2:
                return tab3;
        }
        return null;
    }

    @Override
    public int getCount(){
        return numoftabs;
    }

    public int getItemPosition(@NonNull Object object){
        return POSITION_NONE;
    }

}
package com.penpendev.pair;

import android.view.MotionEvent;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;



public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();



    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
        mFragmentList.add(new MemberManagement());
        mFragmentList.add(new EntryMember());
        mFragmentList.add(new Battle());
        mFragmentList.add(new SettingsFragment());
    }

    public void changeFlagment(int position,Fragment flagment) {
        mFragmentList.set(position,flagment);
    }

    @Override
    public Fragment getItem(int position) {

        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();

    }



    @Override
    public CharSequence getPageTitle(int position) {
        return "ページ" + (position + 1);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }



    @Override
    public int getItemPosition(Object object) {
        super.getItemPosition(object);
        Fragment target = (Fragment) object;
        if (mFragmentList.contains(target)) {
            return POSITION_NONE;
        }

        return POSITION_NONE;
    }


}
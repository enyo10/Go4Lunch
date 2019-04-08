package ch.enyo.openclassrooms.go4lunch.views;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG=MyPagerAdapter.class.getSimpleName();

private List<BaseFragment> mBaseFragmentList=new ArrayList<>();
private List<String>mFragmentTitleList=new ArrayList<>();



    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return mBaseFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mBaseFragmentList.size();
    }


    public void addFragment(BaseFragment fragment){
        mBaseFragmentList.add(fragment);
        mFragmentTitleList.add(fragment.name);
        Log.i(TAG, "fragment name "+fragment.name);

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Log.i(TAG, "in get Title "+ mFragmentTitleList.get(position));
        return mFragmentTitleList.get(position);
    }
}

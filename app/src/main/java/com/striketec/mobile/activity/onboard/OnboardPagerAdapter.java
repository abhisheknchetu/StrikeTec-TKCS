package com.striketec.mobile.activity.onboard;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class OnboardPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public OnboardPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        if(position == 0) {
            f = OnboardFirstFragment.newInstance(mContext);
        } else if(position == 1) {
            f = OnboardSecondFragment.newInstance(mContext);
        } else if(position == 2) {
            f = OnboardThirdFragment.newInstance(mContext);
        } else if (position == 3){
            f = OnboardFourFragment.newInstance(mContext);
        }
        return f;
    }

    @Override
    public int getCount() {
        return 4;
    }

}

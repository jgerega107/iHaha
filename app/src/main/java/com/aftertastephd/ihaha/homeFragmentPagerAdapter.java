package com.aftertastephd.ihaha;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.aftertastephd.ihaha.CameraFlow.cameraFragment;
import com.aftertastephd.ihaha.ChatsFlow.chatsFragment;

public class homeFragmentPagerAdapter extends FragmentPagerAdapter {

    public homeFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new chatsFragment();
        } else {
            return new cameraFragment();
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position){
        if(position == 0){
            return "Conversations";
        }
        else{
            return "Camera";
        }
    }
}

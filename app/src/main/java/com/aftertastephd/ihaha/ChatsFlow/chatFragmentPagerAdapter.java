package com.aftertastephd.ihaha.ChatsFlow;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aftertastephd.ihaha.ChatsFlow.GroupsFlow.groupsOverviewFragment;
import com.aftertastephd.ihaha.ChatsFlow.MessagesFlow.conversationOverviewFragment;

public class chatFragmentPagerAdapter extends FragmentPagerAdapter {
    public chatFragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new conversationOverviewFragment();
        } else{
            return new groupsOverviewFragment();
        }
        //focus on group implementation later
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position){
        if(position == 0){
            return "DMs";
        }
        else{
            return "Groups";
        }
    }
}

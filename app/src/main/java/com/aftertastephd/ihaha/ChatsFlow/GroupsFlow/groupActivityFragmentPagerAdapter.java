package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class groupActivityFragmentPagerAdapter extends FragmentPagerAdapter {
    private String inviteCode;
    public groupActivityFragmentPagerAdapter(FragmentManager fm, String inviteCode) {
        super(fm);
        this.inviteCode = inviteCode;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            groupFragment fragment = new groupFragment();
            Bundle b = new Bundle();
            b.putString("GROUP_INVITECODE", inviteCode);
            fragment.setArguments(b);
            return fragment;
        } else if(position == 1){
            groupInformationFragment fragment = new groupInformationFragment();
            Bundle b = new Bundle();
            b.putString("GROUP_INVITECODE", inviteCode);
            fragment.setArguments(b);
            return fragment;
        }
        else{
            groupMemberListFragment fragment = new groupMemberListFragment();
            Bundle b = new Bundle();
            b.putString("GROUP_INVITECODE", inviteCode);
            fragment.setArguments(b);
            return fragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}

package com.aftertastephd.ihaha.ChatsFlow.MessagesFlow;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class conversationActivityFragmentPagerAdapter extends FragmentPagerAdapter {
    private String uid;
    public conversationActivityFragmentPagerAdapter(FragmentManager fm, String uid) {
        super(fm);
        this.uid = uid;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            conversationFragment fragment = new conversationFragment();
            Bundle b = new Bundle();
            b.putString("CONVERSATION_UID", uid);
            fragment.setArguments(b);
            return fragment;
        } else{
            conversationProfileViewer fragment = new conversationProfileViewer();
            Bundle b = new Bundle();
            b.putString("CONVERSATION_UID", uid);
            fragment.setArguments(b);
            return fragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}

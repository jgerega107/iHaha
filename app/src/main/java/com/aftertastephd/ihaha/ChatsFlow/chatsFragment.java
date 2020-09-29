package com.aftertastephd.ihaha.ChatsFlow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.aftertastephd.ihaha.R;
import com.google.android.material.tabs.TabLayout;

public class chatsFragment extends Fragment {

    private ViewPager chatsPager;
    private TabLayout tabLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chats, container, false);
        chatsPager = v.findViewById(R.id.chatsViewPager);
        chatFragmentPagerAdapter adapter = new chatFragmentPagerAdapter(getChildFragmentManager());
        chatsPager.setAdapter(adapter);
        tabLayout = v.findViewById(R.id.chatsTabLayout);
        tabLayout.setupWithViewPager(chatsPager);

        return v;
    }
}

package com.example.tripline.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tripline.R;
import com.example.tripline.databinding.FragmentFollowTabsBinding;
import com.google.android.material.tabs.TabLayout;

public class FollowTabsFragment extends Fragment {

    public static final String TAG = "FollowTabsFragment";
    private FragmentFollowTabsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFollowTabsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int tabIndex = getArguments().getInt("tabIndex");
        TabLayout.Tab tabToSelect = binding.tabLayoutFollows.getTabAt(tabIndex);
        binding.tabLayoutFollows.selectTab(tabToSelect);
        if (tabToSelect.getPosition() == 0) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.clFollowTabs, new FollowerFragment()).commit();
        } else if (tabToSelect.getPosition() == 1) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.clFollowTabs, new FollowingFragment()).commit();
        }

        binding.tabLayoutFollows.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                if (tab.getPosition() == 0) {  //  display followers fragment
                    fragment = new FollowerFragment();
                } else if (tab.getPosition() == 1) {  // display following fragment
                    fragment = new FollowingFragment();
                }
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.clFollowTabs, fragment).commit();
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

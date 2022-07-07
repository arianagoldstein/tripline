package com.example.tripline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripline.adapters.FollowingAdapter;
import com.example.tripline.databinding.FragmentFollowingBinding;
import com.example.tripline.models.User;

import java.util.ArrayList;
import java.util.List;

public class FollowingFragment extends Fragment {

    public static final String TAG = "FollowingFragment";
    private FragmentFollowingBinding binding;
    protected FollowingAdapter adapter;
    protected List<User> allFollowing;

    public FollowingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFollowingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allFollowing = MainActivity.userFollowing;
        adapter = new FollowingAdapter(getContext(), allFollowing);
        binding.rvFollowing.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.rvFollowing.setLayoutManager(llm);
    }
}

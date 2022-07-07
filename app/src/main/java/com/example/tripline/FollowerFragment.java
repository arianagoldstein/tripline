package com.example.tripline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripline.adapters.FollowerAdapter;
import com.example.tripline.databinding.FragmentFollowerBinding;
import com.example.tripline.models.User;

import java.util.ArrayList;
import java.util.List;

public class FollowerFragment extends Fragment {

    public static final String TAG = "FollowerFragment";
    private FragmentFollowerBinding binding;
    protected FollowerAdapter adapter;
    protected List<User> allFollowers;

    public FollowerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFollowerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allFollowers = MainActivity.userFollowers;
        adapter = new FollowerAdapter(getContext(), allFollowers);
        binding.rvFollowers.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.rvFollowers.setLayoutManager(llm);
    }
}

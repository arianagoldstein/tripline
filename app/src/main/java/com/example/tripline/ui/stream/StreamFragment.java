package com.example.tripline.ui.stream;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tripline.databinding.FragmentStreamBinding;

// this fragment will display a stream of trips created by the logged-in user and other users
public class StreamFragment extends Fragment {

    private FragmentStreamBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StreamViewModel homeViewModel =
                new ViewModelProvider(this).get(StreamViewModel.class);

        binding = FragmentStreamBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textStream;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.example.tripline.ui.addtrip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tripline.databinding.FragmentAddtripBinding;

// this fragment will allow the user to add a new trip to their profile
public class AddTripFragment extends Fragment {

    private FragmentAddtripBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddTripViewModel addTripViewModel =
                new ViewModelProvider(this).get(AddTripViewModel.class);

        binding = FragmentAddtripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
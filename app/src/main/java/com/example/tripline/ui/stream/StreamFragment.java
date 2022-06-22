package com.example.tripline.ui.stream;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripline.TripAdapter;
import com.example.tripline.databinding.FragmentStreamBinding;
import com.example.tripline.models.Trip;

import java.util.ArrayList;
import java.util.List;

// this fragment will display a stream of trips created by the logged-in user and other users
public class StreamFragment extends Fragment {

    private FragmentStreamBinding binding;
    private RecyclerView rvTrips;
    protected TripAdapter adapter;
    protected List<Trip> allTrips;

    public StreamFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StreamViewModel homeViewModel =
                new ViewModelProvider(this).get(StreamViewModel.class);

        binding = FragmentStreamBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // rvTrips = view.findViewById(R.id.rvTrips);

        allTrips = new ArrayList<>();
        adapter = new TripAdapter(getContext(), allTrips);
        // binding.rvTrips.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        // rvTrips.setLayoutManager(llm);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

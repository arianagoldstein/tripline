package com.example.tripline.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripline.R;
import com.example.tripline.databinding.ItemTripSearchRecBinding;
import com.example.tripline.models.Trip;
import com.example.tripline.viewmodels.TripViewModel;

import java.util.List;

public class TripSavedAdapter extends RecyclerView.Adapter<TripSavedAdapter.ViewHolder> {

    public static final String TAG = "TripSavedAdapter";
    private final Context context;
    private List<Trip> savedTrips;
    private final TripViewModel tripViewModel;

    public TripSavedAdapter(Context context, List<Trip> trips) {
        this.context = context;
        this.savedTrips = trips;
        tripViewModel = ViewModelProviders.of((FragmentActivity) context).get(TripViewModel.class);
    }

    @NonNull
    @Override
    public TripSavedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripSearchRecBinding binding = ItemTripSearchRecBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripSavedAdapter.ViewHolder holder, int position) {
        Trip trip = savedTrips.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        if (savedTrips != null) {
            return savedTrips.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemTripSearchRecBinding binding;

        public ViewHolder(ItemTripSearchRecBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.cvTripRec.setOnClickListener(this);
        }

        public void bind(Trip trip) {
            binding.tvTripTitleSearchRec.setText(trip.getTitle());
            Glide.with(context).load(trip.getCoverPhoto().getUrl()).into(binding.ivTripCoverPhotoSearchRec);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Trip trip = savedTrips.get(position);
                tripViewModel.setSelectedTrip(trip);
                NavController navController = Navigation.findNavController(itemView);
                navController.navigate(R.id.action_navigation_saved_to_navigation_tripdetails);
            }
        }
    }
}

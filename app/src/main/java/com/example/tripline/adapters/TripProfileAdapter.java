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
import com.example.tripline.databinding.ItemTripProfileBinding;
import com.example.tripline.models.Trip;
import com.example.tripline.viewmodels.TripViewModel;

import java.util.List;

public class TripProfileAdapter extends RecyclerView.Adapter<TripProfileAdapter.ViewHolder> {

    public static final String TAG = "TripProfileAdapter";
    private Context context;
    private List<Trip> trips;
    private TripViewModel tripViewModel;

    public TripProfileAdapter(Context context, List<Trip> trips) {
        this.context = context;
        this.trips = trips;
        tripViewModel = ViewModelProviders.of((FragmentActivity) context).get(TripViewModel.class);
    }

    @NonNull
    @Override
    public TripProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripProfileBinding binding = ItemTripProfileBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripProfileAdapter.ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemTripProfileBinding binding;

        public ViewHolder(ItemTripProfileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Trip trip) {
            // populating the XML elements with the details of this trip
            binding.tvTripTitleProfile.setText(trip.getTitle());
            binding.tvLocationProfile.setText(trip.getFormattedLocation());
            binding.tvStartDateProfile.setText(trip.getFormattedDate(trip.getStartDate()) + " - ");
            binding.tvEndDateProfile.setText(trip.getFormattedDate(trip.getEndDate()));
            Glide.with(context).load(trip.getCoverPhoto().getUrl()).into(binding.ivCoverPhotoProfile);
        }

        // getting the trip that the user clicked on and passing it to the details page to display it
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Trip trip = trips.get(position);
                tripViewModel.setSelectedTrip(trip);
                NavController navController = Navigation.findNavController(itemView);
                navController.navigate(R.id.action_navigation_profile_to_navigation_tripdetails);
            }
        }
    }
}

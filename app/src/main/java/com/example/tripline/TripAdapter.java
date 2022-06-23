package com.example.tripline;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripline.databinding.ItemTripStreamBinding;
import com.example.tripline.models.Trip;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private Context context;
    private List<Trip> trips;

    public TripAdapter(Context context, List<Trip> trips) {
        this.context = context;
        this.trips = trips;
    }

    @NonNull
    @Override
    public TripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripStreamBinding binding = ItemTripStreamBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripAdapter.ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemTripStreamBinding binding;

        public ViewHolder(ItemTripStreamBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Trip trip) {
            // populating the XML elements with the details of this trip
            binding.tvTripTitleStream.setText(trip.getTitle());
            binding.tvLocationStream.setText(trip.getLocation().toString());
            binding.tvStartDateStream.setText(trip.getFormattedDate(trip.getStartDate()) + " - ");
            binding.tvEndDateStream.setText(trip.getFormattedDate(trip.getEndDate()));
            Glide.with(context).load(trip.getCoverPhoto().getUrl()).into(binding.ivCoverPhotoStream);
            binding.tvDescriptionStream.setText(trip.getDescription());
            binding.tvAuthorNameStream.setText(trip.getAuthor().getFirstName() + " " + trip.getAuthor().getLastName());
        }

        @Override
        public void onClick(View v) {

        }

    }
}

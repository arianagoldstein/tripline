package com.example.tripline;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripline.databinding.ItemTripProfileBinding;
import com.example.tripline.databinding.ItemTripStreamBinding;
import com.example.tripline.models.Trip;

import java.util.List;

public class TripAdapterProfile extends RecyclerView.Adapter<TripAdapterProfile.ViewHolder> {

    public static final String TAG = "TripAdapterProfile";
    private Context context;
    private List<Trip> trips;

    public TripAdapterProfile(Context context, List<Trip> trips) {
        this.context = context;
        this.trips = trips;
    }

    @NonNull
    @Override
    public TripAdapterProfile.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripProfileBinding binding = ItemTripProfileBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripAdapterProfile.ViewHolder holder, int position) {
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
            binding.tvLocationProfile.setText(trip.getLocation().toString());
            binding.tvStartDateProfile.setText(trip.getFormattedDate(trip.getStartDate()) + " - ");
            binding.tvEndDateProfile.setText(trip.getFormattedDate(trip.getEndDate()));
            Glide.with(context).load(trip.getCoverPhoto().getUrl()).into(binding.ivCoverPhotoProfile);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "trip clicked!");
            int position = getAdapterPosition();

            // getting the trip that the user clicked on and passing it to the details page to display it
            if (position != RecyclerView.NO_POSITION) {
                Trip trip = trips.get(position);
                Intent i = new Intent(context, TripDetailsActivity.class);
                i.putExtra("trip", trip);
                context.startActivity(i);
            }
        }

    }
}

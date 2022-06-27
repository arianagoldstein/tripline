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
import com.example.tripline.databinding.ItemTripStreamBinding;
import com.example.tripline.models.Trip;

import java.util.List;

public class TripAdapterStream extends RecyclerView.Adapter<TripAdapterStream.ViewHolder> {

    public static final String TAG = "TripAdapterStream";
    private Context context;
    private List<Trip> trips;

    public TripAdapterStream(Context context, List<Trip> trips) {
        this.context = context;
        this.trips = trips;
    }

    @NonNull
    @Override
    public TripAdapterStream.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripStreamBinding binding = ItemTripStreamBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripAdapterStream.ViewHolder holder, int position) {
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
            binding.getRoot().setOnClickListener(this);
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

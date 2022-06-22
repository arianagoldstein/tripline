package com.example.tripline;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip_stream, parent, false);
        return new ViewHolder(view);
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

        // declaring XML elements
        private final TextView tvTripTitle;
        private final TextView tvLocation;
        private final TextView tvStartDate;
        private final TextView tvEndDate;
        private final ImageView ivCoverPhoto;
        private final TextView tvDescription;
        private final TextView tvAuthorName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // locating the XML elements in the layout
            tvTripTitle = itemView.findViewById(R.id.tvTripTitleStream);
            tvLocation = itemView.findViewById(R.id.tvLocationStream);
            tvStartDate = itemView.findViewById(R.id.tvStartDateStream);
            tvEndDate = itemView.findViewById(R.id.tvEndDateStream);
            ivCoverPhoto = itemView.findViewById(R.id.ivCoverPhotoStream);
            tvDescription = itemView.findViewById(R.id.tvDescriptionStream);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorNameStream);

            itemView.setOnClickListener(this);
        }

        public void bind(Trip trip) {
            // populating the XML elements with the details of this trip
            tvTripTitle.setText(trip.getTitle());
            tvLocation.setText(trip.getLocation().toString());
            tvStartDate.setText(trip.getStartDate().toString());
            tvEndDate.setText(trip.getEndDate().toString());
            Glide.with(context).load(trip.getCoverPhoto().getUrl()).into(ivCoverPhoto);
            tvDescription.setText(trip.getDescription());
            tvAuthorName.setText(trip.getAuthor().getFirstName() + " " + trip.getAuthor().getLastName());
        }

        @Override
        public void onClick(View v) {

        }

    }
}

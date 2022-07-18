package com.example.tripline.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripline.R;
import com.example.tripline.viewmodels.TripViewModel;
import com.example.tripline.databinding.ItemTripProfileBinding;
import com.example.tripline.models.Trip;

import java.util.ArrayList;
import java.util.List;

public class TripSearchAdapter extends RecyclerView.Adapter<TripSearchAdapter.ViewHolder> implements Filterable {

    public static final String TAG = "TripSearchAdapter";
    private Context context;
    private List<Trip> allTrips;
    private List<Trip> filteredTrips;
    private TripViewModel tripViewModel;

    public TripSearchAdapter(Context context, List<Trip> trips) {
        this.context = context;
        this.allTrips = trips;
        tripViewModel = ViewModelProviders.of((FragmentActivity) context).get(TripViewModel.class);
    }

    @NonNull
    @Override
    public TripSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripProfileBinding binding = ItemTripProfileBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripSearchAdapter.ViewHolder holder, int position) {
        Trip trip = filteredTrips.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        if (filteredTrips != null) {
            return filteredTrips.size();
        } else {
            return 0;
        }
    }

    // filtering search results to include trips where substrings match
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String queryString = constraint.toString();
                if (queryString.isEmpty()) {
                    filteredTrips = allTrips;
                } else {
                    List<Trip> filteredList = new ArrayList<>();
                    for (Trip trip : allTrips) {
                        if (trip.getTitle().toLowerCase().contains(queryString.toLowerCase()) || trip.getFormattedLocation().toLowerCase().contains(queryString.toLowerCase())) {
                            filteredList.add(trip);
                        }
                    }
                    filteredTrips = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredTrips;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredTrips = (ArrayList<Trip>) results.values;
                notifyDataSetChanged();
            }
        };
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
                Trip trip = filteredTrips.get(position);
                tripViewModel.setSelectedTrip(trip);
                NavController navController = Navigation.findNavController(itemView);
                navController.navigate(R.id.action_navigation_search_to_navigation_tripdetails);
            }
        }
    }
}


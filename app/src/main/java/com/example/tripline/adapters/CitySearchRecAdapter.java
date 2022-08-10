package com.example.tripline.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripline.databinding.ItemCitySearchRecBinding;
import com.example.tripline.models.City;
import com.example.tripline.viewmodels.SearchViewModel;

import java.util.List;

public class CitySearchRecAdapter extends RecyclerView.Adapter<CitySearchRecAdapter.ViewHolder> {

    public static final String TAG = "CitySearchRecAdapter";
    private Context context;
    private List<City> cities;
    private SearchViewModel searchViewModel;

    public CitySearchRecAdapter(Context context, List<City> cities) {
        this.context = context;
        this.cities = cities;
        searchViewModel = ViewModelProviders.of((FragmentActivity) context).get(SearchViewModel.class);
    }

    @NonNull
    @Override
    public CitySearchRecAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCitySearchRecBinding binding = ItemCitySearchRecBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CitySearchRecAdapter.ViewHolder holder, int position) {
        City city = cities.get(position);
        holder.bind(city);
    }

    @Override
    public int getItemCount() {
        if (cities != null) {
            return cities.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemCitySearchRecBinding binding;

        public ViewHolder(ItemCitySearchRecBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.cvCityRec.setOnClickListener(this);
        }

        public void bind(City city) {
            binding.tvCityNameSearchRec.setText(city.getCityName());
            Glide.with(context).load(city.getImage().getUrl()).into(binding.ivCityCoverPhotoSearchRec);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                City city = cities.get(position);
                Log.i(TAG, "City: " + city.getCityName());
                searchViewModel.setCity(city);
            }
        }
    }
}

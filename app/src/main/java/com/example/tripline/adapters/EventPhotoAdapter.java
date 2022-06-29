package com.example.tripline.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripline.databinding.ItemEventPhotoBinding;
import com.example.tripline.models.Photo;

import java.util.List;

public class EventPhotoAdapter extends RecyclerView.Adapter<EventPhotoAdapter.ViewHolder> {

    public static final String TAG = "EventPhotoAdapter";
    private Context context;
    private List<Photo> photos;

    public EventPhotoAdapter(Context context, List<Photo> photos) {
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public EventPhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventPhotoBinding binding = ItemEventPhotoBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventPhotoAdapter.ViewHolder holder, int position) {
        Photo photo = photos.get(position);
        holder.bind(photo);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemEventPhotoBinding binding;

        public ViewHolder(ItemEventPhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Photo photo) {
            Glide.with(context).load(photo.getUrl()).into(binding.ivEventPhoto);
        }
    }
}

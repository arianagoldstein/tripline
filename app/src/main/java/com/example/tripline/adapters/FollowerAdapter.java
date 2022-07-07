package com.example.tripline.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripline.databinding.ItemFollowerBinding;
import com.example.tripline.models.User;

import java.util.List;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {

    public static final String TAG = "FollowerAdapter";
    private Context context;
    private List<User> followers;

    public FollowerAdapter(Context context, List<User> followers) {
        this.context = context;
        this.followers = followers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFollowerBinding binding = ItemFollowerBinding.inflate(LayoutInflater.from(context));
        return new FollowerAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User follower = followers.get(position);
        holder.bind(follower);
    }

    @Override
    public int getItemCount() {
        return followers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemFollowerBinding binding;

        public ViewHolder(ItemFollowerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(User follower) {
            binding.tvFollowerName.setText(follower.getFirstName() + " " + follower.getLastName());
        }
    }
}

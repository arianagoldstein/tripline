package com.example.tripline.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripline.databinding.ItemFollowingBinding;
import com.example.tripline.models.User;

import java.util.List;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder> {

    public static final String TAG = "FollowingAdapter";
    private Context context;
    private List<User> following;

    public FollowingAdapter(Context context, List<User> following) {
        this.context = context;
        this.following = following;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFollowingBinding binding = ItemFollowingBinding.inflate(LayoutInflater.from(context));
        return new FollowingAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User followingU = following.get(position);
        holder.bind(followingU);
    }

    @Override
    public int getItemCount() {
        return following.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemFollowingBinding binding;

        public ViewHolder(ItemFollowingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(User followingU) {
            binding.tvFollowingName.setText(followingU.getFirstName() + " " + followingU.getLastName());
        }
    }
}

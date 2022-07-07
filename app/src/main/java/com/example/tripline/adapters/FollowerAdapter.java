package com.example.tripline.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripline.databinding.ItemFollowerBinding;
import com.example.tripline.models.User;
import com.example.tripline.models.UserFollower;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
            Glide.with(context).load(follower.getProfilePic().getUrl()).into(binding.ivProfilePicFollower);
            binding.btnRemoveFollower.setOnClickListener(v -> onRemoveBtnClicked(follower));
        }

        private void onRemoveBtnClicked(User follower) {
            ParseQuery<UserFollower> query = ParseQuery.getQuery(UserFollower.class);
            query.whereEqualTo(UserFollower.KEY_USER_ID, ParseUser.getCurrentUser());
            query.whereEqualTo(UserFollower.KEY_FOLLOWER_ID, follower);
            query.findInBackground((objects, e) -> removeFollower(objects, e));
        }

        private void removeFollower(List<UserFollower> objects, ParseException e) {
            if (e != null) {
                Log.e(TAG, "Issue finding user to remove", e);
            }
            for (UserFollower object : objects) {
                try {
                    object.delete();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                object.saveInBackground();
            }
        }
    }
}

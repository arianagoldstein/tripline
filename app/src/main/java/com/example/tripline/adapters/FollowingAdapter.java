package com.example.tripline.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripline.MainActivity;
import com.example.tripline.R;
import com.example.tripline.databinding.ItemFollowingBinding;
import com.example.tripline.models.User;
import com.example.tripline.models.UserFollower;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
            Glide.with(context).load(followingU.getProfilePic().getUrl()).into(binding.ivProfilePicFollowing);

            // if this is someone else's profile, we shouldn't be able to unfollow
            if (!(MainActivity.userToDisplay.hasSameId(ParseUser.getCurrentUser()))) {
                binding.btnUnfollow.setVisibility(View.GONE);
            } else {
                binding.btnUnfollow.setVisibility(View.VISIBLE);
                binding.btnUnfollow.setOnClickListener(v -> onUnfollowBtnClicked(followingU));
                binding.btnUnfollow.setBackgroundColor(context.getColor(R.color.turquoise));
                binding.btnUnfollow.setText(R.string.unfollow);
            }
        }

        private void onUnfollowBtnClicked(User followingU) {
            ParseQuery<UserFollower> query = ParseQuery.getQuery(UserFollower.class);
            query.whereEqualTo(UserFollower.KEY_USER_ID, followingU);
            query.whereEqualTo(UserFollower.KEY_FOLLOWER_ID, ParseUser.getCurrentUser());
            query.findInBackground((objects, e) -> unfollowUser(objects, e));
            binding.btnUnfollow.setBackgroundColor(context.getColor(R.color.gray));
            binding.btnUnfollow.setText(R.string.unfollowed);
        }

        private void unfollowUser(List<UserFollower> objects, ParseException e) {
            if (e != null) {
                Log.e(TAG, "Issue finding user to unfollow", e);
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

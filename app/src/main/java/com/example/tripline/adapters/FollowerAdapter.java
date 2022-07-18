package com.example.tripline.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tripline.R;
import com.example.tripline.viewmodels.UserViewModel;
import com.example.tripline.databinding.ItemFollowerBinding;
import com.example.tripline.models.User;
import com.example.tripline.models.UserFollower;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {

    public static final String TAG = "FollowerAdapter";
    private Context context;
    private List<User> followers;
    private UserViewModel sharedViewModel;

    public FollowerAdapter(Context context, List<User> followers) {
        this.context = context;
        this.followers = followers;
        sharedViewModel = ViewModelProviders.of((FragmentActivity) context).get(UserViewModel.class);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemFollowerBinding binding;

        public ViewHolder(ItemFollowerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        // displaying information about this follower
        public void bind(User follower) {
            binding.tvFollowerName.setText(follower.getFirstName() + " " + follower.getLastName());
            Glide.with(context).load(follower.getProfilePic().getUrl()).circleCrop().into(binding.ivProfilePicFollower);

            // if this is someone else's profile, we shouldn't be able to remove followers
            if (!(sharedViewModel.isCurrentUser())) {
                binding.btnRemoveFollower.setVisibility(View.GONE);
            } else {
                binding.btnRemoveFollower.setVisibility(View.VISIBLE);
                binding.btnRemoveFollower.setOnClickListener(v -> onRemoveBtnClicked(follower));
                binding.btnRemoveFollower.setBackgroundColor(context.getColor(R.color.turquoise));
                binding.btnRemoveFollower.setText(R.string.remove);
            }
        }

        // queries Parse to remove this follower from the UserFollower table
        private void onRemoveBtnClicked(User follower) {
            ParseQuery<UserFollower> query = ParseQuery.getQuery(UserFollower.class);
            query.whereEqualTo(UserFollower.KEY_USER_ID, ParseUser.getCurrentUser());
            query.whereEqualTo(UserFollower.KEY_FOLLOWER_ID, follower);
            query.findInBackground((objects, e) -> removeFollower(objects, e));
            binding.btnRemoveFollower.setBackgroundColor(context.getColor(R.color.gray));
            binding.btnRemoveFollower.setText(R.string.removed);
        }

        // deleting this follower from Parse
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

        // go to the other user's profile when the user clicks on it
        @Override
        public void onClick(View v) {
            Log.i(TAG, "A follower was clicked!");
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                User follower = followers.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("source", "followerAdapter");
                sharedViewModel.setUserToDisplay(follower);
                NavController navController = Navigation.findNavController(itemView);
                navController.navigate(R.id.action_navigation_follower_to_navigation_profile, bundle);

            }
        }
    }
}

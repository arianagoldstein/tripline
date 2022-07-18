package com.example.tripline.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripline.adapters.FollowingAdapter;
import com.example.tripline.databinding.FragmentFollowingBinding;
import com.example.tripline.models.User;
import com.example.tripline.models.UserFollower;
import com.example.tripline.viewmodels.UserViewModel;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FollowingFragment extends Fragment {

    public static final String TAG = "FollowingFragment";
    private FragmentFollowingBinding binding;
    protected FollowingAdapter adapter;
    protected List<User> allFollowing;
    private User user;
    private UserViewModel sharedViewModel;

    public FollowingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = ViewModelProviders.of(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFollowingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = sharedViewModel.getUserToDisplay();

        allFollowing = new ArrayList<>();
        getFollowing();
        adapter = new FollowingAdapter(getContext(), allFollowing);
        binding.rvFollowing.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.rvFollowing.setLayoutManager(llm);

        binding.swipeContainerFollowing.setOnRefreshListener(() -> getFollowing());
    }

    // getting the followers of the user we're displaying
    protected void getFollowing() {
        // we want to get the Users that the logged-in user follows, the following
        ParseQuery<UserFollower> query = ParseQuery.getQuery(UserFollower.class);
        query.include(UserFollower.KEY_USER_ID);
        query.include(UserFollower.KEY_FOLLOWER_ID);
        query.whereEqualTo(UserFollower.KEY_FOLLOWER_ID, user);

        query.findInBackground((userFollowers, e) -> addFollowing(userFollowers, e));
    }

    // once we get the list of following, we add them to our list to display in the RecyclerView
    private void addFollowing(List<UserFollower> userFollowers, ParseException e) {
        binding.swipeContainerFollowing.setRefreshing(false);
        if (e != null) {
            Log.e(TAG, "Issue getting following for user " + user.getFirstName(), e);
        }

        // at this point, we have gotten the user-follower list successfully
        allFollowing.clear();
        for (UserFollower uf : userFollowers) {
            allFollowing.add(uf.getUserF());
        }
        adapter.notifyDataSetChanged();
    }
}

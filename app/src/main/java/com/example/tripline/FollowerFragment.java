package com.example.tripline;

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

import com.example.tripline.adapters.FollowerAdapter;
import com.example.tripline.databinding.FragmentFollowerBinding;
import com.example.tripline.models.User;
import com.example.tripline.models.UserFollower;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FollowerFragment extends Fragment {

    public static final String TAG = "FollowerFragment";
    private FragmentFollowerBinding binding;
    protected FollowerAdapter adapter;
    protected List<User> allFollowers;
    private User user;
    private TripViewModel sharedViewModel;

    public FollowerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = ViewModelProviders.of(requireActivity()).get(TripViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFollowerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = sharedViewModel.getUserToDisplay();

        allFollowers = new ArrayList<>();
        getFollowers();
        adapter = new FollowerAdapter(getContext(), allFollowers);
        binding.rvFollowers.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.rvFollowers.setLayoutManager(llm);

        binding.swipeContainerFollower.setOnRefreshListener(() -> getFollowers());
    }

    // getting the followers of the user we're displaying
    protected void getFollowers() {
        // we want to get the Users that FOLLOW the logged-in user, the followers
        ParseQuery<UserFollower> query = ParseQuery.getQuery(UserFollower.class);
        query.include(UserFollower.KEY_USER_ID);
        query.include(UserFollower.KEY_FOLLOWER_ID);
        query.whereEqualTo(UserFollower.KEY_USER_ID, user);

        query.findInBackground((userFollowers, e) -> addFollowers(userFollowers, e));
    }

    // once we get the list of followers, we add them to our list to display in the RecyclerView
    private void addFollowers(List<UserFollower> userFollowers, ParseException e) {
        binding.swipeContainerFollower.setRefreshing(false);
        if (e != null) {
            Log.e(TAG, "Issue getting followers for user " + user.getFirstName(), e);
        }

        // at this point, we have gotten the user-follower list successfully
        allFollowers.clear();
        for (UserFollower uf : userFollowers) {
            allFollowers.add(uf.getFollower());
        }
        adapter.notifyDataSetChanged();
    }
}

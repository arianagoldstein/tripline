package com.example.tripline.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tripline.MainActivity;
import com.example.tripline.databinding.FragmentProfileBinding;
import com.example.tripline.models.User;

// this fragment will display the profile of the user who is currently logged in
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    public static final String TAG = "ProfileFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        User userToDisplay = MainActivity.currentUser;
        Log.i(TAG, "Displaying profile for user " + userToDisplay.getFirstName() + " " + userToDisplay.getLastName());
        binding.tvNameProfile.setText(userToDisplay.getFirstName() + " " + userToDisplay.getLastName());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
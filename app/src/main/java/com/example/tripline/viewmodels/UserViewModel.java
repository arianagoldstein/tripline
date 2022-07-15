package com.example.tripline.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.tripline.models.User;
import com.parse.ParseUser;

public class UserViewModel extends ViewModel {

    public static final String TAG = "MyViewModel";

    private User userToDisplay;

    public void setUserToDisplay(User user) {
        userToDisplay = user;
    }

    public User getUserToDisplay() {
        return userToDisplay;
    }

    public boolean isCurrentUser() {
        return (userToDisplay != null) && userToDisplay.hasSameId(ParseUser.getCurrentUser());
    }
}

package com.example.tripline.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

// a UserFollower object represents a user-follower pair
@ParseClassName("UserFollower")
public class UserFollower extends ParseObject {

    public static final String KEY_USER_ID = "userId";
    public static final String KEY_FOLLOWER_ID = "followerId";

    public UserFollower() {
    }

    public User getUserF() {
        return (User) getParseUser(KEY_USER_ID);
    }

    public void setUserF(User user) {
        put(KEY_USER_ID, user);
    }

    public User getFollower() {
        return (User) getParseUser(KEY_FOLLOWER_ID);
    }

    public void setFollower(User follower) {
        put(KEY_FOLLOWER_ID, follower);
    }

}

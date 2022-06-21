package com.example.tripline.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

// a User represents a user of the app who can log in and add/view trips
@ParseClassName("_User")
public class User extends ParseUser {

    // only keep track of keys/getters/setters for custom fields (the ones not default with Parse like username and password)
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_PROFILE_PIC = "profilePic";

    public User() {
    }

    public String getFirstName() {
        return getString(KEY_FIRST_NAME);
    }

    public void setFirstName(String firstName) {
        put(KEY_FIRST_NAME, firstName);
    }

    public String getLastName() {
        return getString(KEY_LAST_NAME);
    }

    public void setLastName(String lastName) {
        put(KEY_LAST_NAME, lastName);
    }

    public ParseFile getProfilePic() {
        return getParseFile(KEY_PROFILE_PIC);
    }

    public void setProfilePic(ParseFile profilePic) {
        put(KEY_PROFILE_PIC, profilePic);
    }
}

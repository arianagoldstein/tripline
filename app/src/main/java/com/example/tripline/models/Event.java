package com.example.tripline.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;

// an Event object represents one event associated with a trip created by a user
@ParseClassName("Event")
public class Event extends ParseObject {

    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PHOTOS = "photos";
    public static final String KEY_ACTIVITY_TYPE = "activityType";
    public static final String KEY_TRIP = "trip";

    public Event() {

    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    // eventually will want this function to return an ArrayList of ParseFiles for ease of display
    public JSONArray getPhotos() {
        return getJSONArray(KEY_PHOTOS);
    }

    public void setPhotos(JSONArray photos) {
        put(KEY_PHOTOS, photos);
    }

    public String getActivityType() {
        return getString(KEY_ACTIVITY_TYPE);
    }

    public void setKeyActivityType(String activityType) {
        put(KEY_ACTIVITY_TYPE, activityType);
    }

    public Trip getTrip() {
        return (Trip) getParseObject(KEY_TRIP);
    }

    public void setTrip(Trip trip) {
        put(KEY_TRIP, trip);
    }

}

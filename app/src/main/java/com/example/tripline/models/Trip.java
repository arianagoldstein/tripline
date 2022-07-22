package com.example.tripline.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// a Trip object represents a trip created by a user that will be displayed on their profile and in the stream
@ParseClassName("Trip")
public class Trip extends ParseObject {

    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";
    public static final String KEY_COVER_PHOTO = "coverPhoto";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_FORMATTED_LOCATION = "formattedLocation";
    public static final String KEY_IS_SAVED = "isSaved";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_CITY = "city";
    public static final String KEY_EVENT_ATTRIBUTES = "eventAttributes";
    public static final String KEY_SAVED_BY = "savedBy";

    public Trip() {

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

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint location) {
        put(KEY_LOCATION, location);
    }

    public Date getStartDate() {
        return getDate(KEY_START_DATE);
    }

    public void setStartDate(Date startDate) {
        put(KEY_START_DATE, startDate);
    }

    public Date getEndDate() {
        return getDate(KEY_END_DATE);
    }

    public void setEndDate(Date endDate) {
        put(KEY_END_DATE, endDate);
    }

    public ParseFile getCoverPhoto() {
        return getParseFile(KEY_COVER_PHOTO);
    }

    public void setCoverPhoto(ParseFile coverPhoto) {
        put(KEY_COVER_PHOTO, coverPhoto);
    }

    public User getAuthor() {
        return (User) getParseUser(KEY_AUTHOR);
    }

    public void setAuthor(User user) {
        put(KEY_AUTHOR, user);
    }

    public String getFormattedLocation() {
        return getString(KEY_FORMATTED_LOCATION);
    }

    public void setFormattedLocation(String location) {
        put(KEY_FORMATTED_LOCATION, location);
    }

    public boolean getIsSaved() {
        return getBoolean(KEY_IS_SAVED);
    }

    public void setIsSaved(boolean saved) {
        put(KEY_IS_SAVED, saved);
    }

    public int getDuration() {
        return getInt(KEY_DURATION);
    }

    public void setDuration(int duration) {
        put(KEY_DURATION, duration);
    }

    public City getCity() {
        return (City) getParseObject(KEY_CITY);
    }

    public void setCity(City city) {
        put(KEY_CITY, city);
    }

    public List<User> getSavedBy() {
        List<User> savedBy = getList(KEY_SAVED_BY);
        if (savedBy == null) {
            return new ArrayList<>();
        }
        return savedBy;
    }

    public void setSavedBy(List<User> savedBy) {
        put(KEY_SAVED_BY, savedBy);
    }

    public boolean isSavedByCurrentUser() {
        List<User> savedBy = getSavedBy();
        for (int i = 0; i < savedBy.size(); i++) {
            if (savedBy.get(i).hasSameId(ParseUser.getCurrentUser())) {
                return true;
            }
        }
        return false;
    }

    public void saveTrip() {
        List<User> savedBy = getSavedBy();
        savedBy.add((User) ParseUser.getCurrentUser());
        setSavedBy(savedBy);
        saveInBackground();
    }

    public void unsaveTrip() {
        List<User> savedBy = getSavedBy();
        for (int i = 0; i < savedBy.size(); i++) {
            if (savedBy.get(i).hasSameId(ParseUser.getCurrentUser())) {
                savedBy.remove(i);
            }
        }
        setSavedBy(savedBy);
        saveInBackground();
    }

    public List<String> getEventAttributes() {
        List<String> eventAttributeList = getList(KEY_EVENT_ATTRIBUTES);
        if (eventAttributeList == null) {
            return new ArrayList<>();
        }
        return eventAttributeList;
    }

    public void setEventAttributes(List<String> eventAttributes) {
        put(KEY_EVENT_ATTRIBUTES, eventAttributes);
    }

    public void updateEventAttributes(String eventType) {
        List<String> currentEventAttributes = getEventAttributes();
        if (!(currentEventAttributes.contains(eventType))) {
            currentEventAttributes.add(eventType);
        }
        put(KEY_EVENT_ATTRIBUTES, currentEventAttributes);
    }

    // takes in a Date and returns a string in mm/dd/yy format
    public String getFormattedDate(Date dateToFormat) {
        Format f = new SimpleDateFormat("MM/dd/yy");
        return f.format(dateToFormat);
    }

}

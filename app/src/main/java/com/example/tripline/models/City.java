package com.example.tripline.models;

import androidx.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("City")
public class City extends ParseObject {

    public static final String KEY_CITY_NAME = "cityName";
    public static final String KEY_IMAGE = "image";

    public @Nullable
    String getCityName() {
        return getString(KEY_CITY_NAME);
    }

    public void setCityName(String cityName) {
        put(KEY_CITY_NAME, cityName);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile file) {
        put(KEY_IMAGE, file);
    }

}

package com.example.tripline;

import android.app.Application;

import com.example.tripline.models.Event;
import com.example.tripline.models.Trip;
import com.example.tripline.models.User;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // registering subclasses User, Trip, and Event
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Trip.class);
        ParseObject.registerSubclass(Event.class);

        // use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // use for monitoring Parse OkHttp traffic
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.application_id))
                .clientKey(getString(R.string.client_key))
                .server(getString(R.string.server))
                .build()
        );
    }
}

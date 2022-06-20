package com.example.tripline.ui.addtrip;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddTripViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AddTripViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is addtrip fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
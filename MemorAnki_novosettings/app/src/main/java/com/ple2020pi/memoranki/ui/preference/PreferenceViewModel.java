package com.ple2020pi.memoranki.ui.preference;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PreferenceViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PreferenceViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is group fragmentt");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
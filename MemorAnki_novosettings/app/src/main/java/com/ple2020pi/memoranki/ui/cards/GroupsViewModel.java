package com.ple2020pi.memoranki.ui.cards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GroupsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GroupsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is group fragmentt");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
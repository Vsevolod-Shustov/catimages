package com.example.android.catimages;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

public class ImageViewModel extends ViewModel {
    private MutableLiveData<ArrayList<String>> mImages;

    public MutableLiveData<ArrayList<String>> getImages() {
        if (mImages == null) {
            mImages = new MutableLiveData<>();
        }
        return mImages;
    }
}

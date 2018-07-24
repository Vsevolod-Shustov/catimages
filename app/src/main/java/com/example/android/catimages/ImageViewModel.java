package com.example.android.catimages;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

public class ImageViewModel extends ViewModel {
    private MutableLiveData<ArrayList<String>> mCurrentImage;

    public MutableLiveData<ArrayList<String>> getCurrentImage() {
        if (mCurrentImage == null) {
            mCurrentImage = new MutableLiveData<>();
        }
        return mCurrentImage;
    }
}

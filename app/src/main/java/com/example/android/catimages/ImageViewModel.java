package com.example.android.catimages;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class ImageViewModel extends ViewModel {
    private MutableLiveData<String> mCurrentImage;

    public MutableLiveData<String> getCurrentImage() {
        if (mCurrentImage == null) {
            mCurrentImage = new MutableLiveData<String>();
        }
        return mCurrentImage;
    }
}

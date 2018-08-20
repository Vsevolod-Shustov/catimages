package com.example.android.catimages;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class SingleImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_image);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }
        Intent intent = getIntent();
        String intentimageurl = intent.getStringExtra("com.example.android.catimages.imageurl");
        ImageView imageView = findViewById(R.id.singleimage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName(intentimageurl);
            Log.d("mytesttag", "detail transition name set to " + intentimageurl);
            GlideApp.with(this)
                    .load(intentimageurl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //tell the compiler it's always true
                                startPostponedEnterTransition();
                            }
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            GlideApp.with(this)
                    .load(intentimageurl)
                    .into(imageView);
        }
        TextView textView = findViewById(R.id.source_backlink);
        textView.append(" " + intentimageurl);
    }
}

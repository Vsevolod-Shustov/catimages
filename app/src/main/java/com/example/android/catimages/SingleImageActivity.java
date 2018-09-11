package com.example.android.catimages;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class SingleImageActivity extends AppCompatActivity {
    ArrayList<String> savedFilesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_image);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();  //Make sure you are extending ActionBarActivity
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);

        if (prefs.getString("savedFilesList", null) != null) {
            savedFilesList = new ArrayList<String>(Arrays.asList(prefs.getString("savedFilesList", null).replaceAll("^\\[|]$", "").split(", ")));
        }


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
                    //.dontAnimate()
                    .dontTransform()
                    .into(imageView);
        } else {
            GlideApp.with(this)
                    .load(intentimageurl)
                    //.dontAnimate()
                    .dontTransform()
                    .into(imageView);
        }

        TextView textView = findViewById(R.id.source_backlink);
        textView.append(" " + intentimageurl);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (savedFilesList.contains(intentimageurl)) {
                    Toast.makeText(getApplicationContext(), "Already saved", Toast.LENGTH_LONG).show();
                } else {
                    BitmapDrawable draw = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bitmap = draw.getBitmap();
                    MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "intentimageurl", "cat image");
                    savedFilesList.add(intentimageurl);
                    prefs.edit().putString("savedFilesList", savedFilesList.toString()).apply();
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

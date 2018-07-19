package com.example.android.catimages;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import static com.example.android.catimages.Parsers.parseXMLForTag;


public class MainActivity extends AppCompatActivity {
    private String requestUrl ="http://thecatapi.com/api/images/get?format=xml&type=jpg&size=med&results_per_page=3";
    //private String imageUrl = "http://24.media.tumblr.com/tumblr_m3dr9lfmr81r73wdao1_500.jpg";
    //private ArrayList<String> imageUrls = new ArrayList<String>();
    private static RequestQueue requestQueue;
    private ImageViewModel mModel;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //loadImagesIntoViews("http://24.media.tumblr.com/tumblr_m3dr9lfmr81r73wdao1_500.jpg");
        //getNewImages();
        if (savedInstanceState == null) {
            getNewImages();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNewImages();
            }
        });

        // Get the ViewModel.
        mModel = ViewModelProviders.of(this).get(ImageViewModel.class);

        // Create the observer which updates the UI.
        final Observer<String> imageObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newImage) {
                // Update the UI
                Log.d("mytesttag", "breakpoint 2: "+ mModel.getCurrentImage());
                ImageView imageView = (ImageView) findViewById(R.id.images);
                GlideApp.with(mContext).load(newImage).into(imageView);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mModel.getCurrentImage().observe(this, imageObserver);
    }

    private void getNewImages () {
        requestQueue.add(stringRequest);
    }

    /*private void loadImagesIntoViews (String url) {
        ImageView imageView = (ImageView) findViewById(R.id.images);
        GlideApp.with(this).load(url).into(imageView);
    }*/

    StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d("mytesttag", "Response is: "+ response);

                    //Parsers.xpathParseXMLForTag(response);
                    ArrayList<String> parsedImageUrl = parseXMLForTag(response, "url");
                    Log.d("mytesttag", "Image URL is: "+ parsedImageUrl);
                    //imageUrls = parsedImageUrl;
                    mModel.getCurrentImage().setValue(parsedImageUrl.get(0));
                    Log.d("mytesttag", "breakpoint 1: "+ mModel.getCurrentImage());
                    //loadImagesIntoViews(imageUrls.get(0));
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("mytesttag", "That didn't work!");
        }
    });
}

package com.example.android.catimages;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private String requestUrl ="http://thecatapi.com/api/images/get?format=xml&type=jpg&size=med&results_per_page=12";
    //private String imageUrl = "http://24.media.tumblr.com/tumblr_m3dr9lfmr81r73wdao1_500.jpg";
    //private ArrayList<String> imageUrls = new ArrayList<String>();
    private static RequestQueue requestQueue;
    private ImageViewModel mModel;
    private Context mContext = this;
    MyRecyclerViewAdapter adapter;

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

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        //int numberOfColumns = 2;
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.column_count)));
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        //recyclerView.setNestedScrollingEnabled(false);
        //recyclerView.setHasFixedSize(true);
        adapter = new MyRecyclerViewAdapter(this, new ArrayList<String>());
        //adapter.setHasStableIds(true);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        // Get the ViewModel.
        mModel = ViewModelProviders.of(this).get(ImageViewModel.class);

        mModel.getCurrentImage().observe(MainActivity.this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> imageUrls) {
                adapter.setData(imageUrls);
            }
        });

        //mModel.getCurrentImage().observe(this, MyRecyclerViewAdapter.setData(mModel.getCurrentImage()));

        // Create the observer which updates the UI.
        /*final Observer<ArrayList<String>> imageObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<String> newImage) {
                // Update the UI
                Log.d("mytesttag", "breakpoint 2: "+ mModel.getCurrentImage());
                //imageUrls = mModel.getCurrentImage();
                adapter = new MyRecyclerViewAdapter(this, mModel.getCurrentImage());
                recyclerView.setAdapter(adapter);
                //ImageView imageView = (ImageView) findViewById(R.id.images);
                //GlideApp.with(mContext).load(newImage).into(imageView);
            }
        };*/

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        //mModel.getCurrentImage().observe(this, imageObserver);


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
                    //mModel.getCurrentImage().setValue(parsedImageUrl.get(0));
                    mModel.getCurrentImage().setValue(parsedImageUrl);
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

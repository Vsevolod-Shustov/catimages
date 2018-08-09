package com.example.android.catimages;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.android.catimages.Parsers.parseXMLForTag;


public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    private String requestUrl ="http://thecatapi.com/api/images/get?format=xml&type=jpg&size=med&results_per_page=12";
    //private String imageUrl = "http://24.media.tumblr.com/tumblr_m3dr9lfmr81r73wdao1_500.jpg";
    //private ArrayList<String> imageUrlsForSavingState = new ArrayList<String>();
    private String imageUrlsForSavingState;
    private static RequestQueue requestQueue;
    private ImageViewModel mModel;
    private Context mContext = this;
    MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //getNewImages();

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
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        //Log.d("mytesttag", "2:" + mModel.getImages().toString());

        // Get the ViewModel.
        mModel = ViewModelProviders.of(this).get(ImageViewModel.class);

        mModel.getImages().observe(MainActivity.this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> imageUrls) {
                adapter.setData(imageUrls);
                //Log.d("mytesttag", "data set to adapter:" + imageUrls);
                //prefs.edit().putString("imageUrls", imageUrls.toString()).apply();
                Set<String> set = new HashSet<String>();
                set.addAll(imageUrls);
                prefs.edit().putStringSet("imageUrls", set).apply();
            }
        });

        if (prefs.getStringSet("imageUrls", null) != null) {
            Log.d("mytesttag", "restoring previous images");

            Set<String> sharedset = prefs.getStringSet("imageUrls", null);
            Log.d("mytesttag", "sharedset: " + sharedset);

            ArrayList<String> sharedlist = new ArrayList<>(sharedset);
            Log.d("mytesttag", "sharedlist: " + sharedlist);

            mModel.getImages().setValue(sharedlist);
        } else {
            Log.d("mytesttag", "getting new images");
            getNewImages();
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        //Log.d("mytesttag", "You clicked image " + adapter.getItem(position) + ", which is at cell position " + position);
        Intent intent = new Intent(this, SingleImageActivity.class);
        String intentimageurl = adapter.getItem(position);
        intent.putExtra("com.example.android.catimages.imageurl", intentimageurl);
        startActivity(intent);
    }

    private void getNewImages () {
        requestQueue.add(stringRequest);
    }

    StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    ArrayList<String> parsedImageUrl = parseXMLForTag(response, "url");
                    //Log.d("mytesttag", "Image URL is: "+ parsedImageUrl);
                    mModel.getImages().setValue(parsedImageUrl);
                    //Log.d("mytesttag", "breakpoint 1: "+ mModel.getImages());
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("mytesttag", "That didn't work!");
        }
    });
}

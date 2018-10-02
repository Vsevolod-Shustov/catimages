package com.example.android.catimages;

import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.android.catimages.Parsers.parseXMLForTag;


public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
//    private String requestUrl ="http://thecatapi.com/api/images/get?format=xml&type=jpg&size=med&results_per_page=12";
    private String requestUrl ="https://api.thecatapi.com/api/images/get.php?format=xml&type=jpg&size=med&results_per_page=12";

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

        android.preference.PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences settings = android.preference.PreferenceManager.getDefaultSharedPreferences(this);

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
        //recyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.column_count)));
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, Integer.parseInt(settings.getString("landscape_columns", "3"))));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, Integer.parseInt(settings.getString("portrait_columns", "2"))));
        }

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
                prefs.edit().putString("imageUrls", imageUrls.toString()).apply();
            }
        });

        if (settings.getBoolean("loadNewImagesOnLaunch", false)) {
            Log.d("mytesttag", "loadNewImagesOnLaunch is true, getting new images");
            getNewImages();
        } else if (prefs.getString("imageUrls", null) != null ) {
            Log.d("mytesttag", "restoring previous images");

            String sharedstring = prefs.getString("imageUrls", null);
            //Log.d("mytesttag", "sharedstring: " + sharedstring);

            ArrayList<String> sharedlist = new ArrayList<String>(Arrays.asList(sharedstring.replaceAll("^\\[|]$", "").split(", ")));
            //Log.d("mytesttag", "sharedlist: " + sharedlist);

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
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    // the context of the activity
                    MainActivity.this,
                    view,
                    intentimageurl);
            ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());
        } else {
            startActivity(intent);
        }

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
            Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_preferences:
                //Toast.makeText(this, "ADD!", Toast.LENGTH_SHORT).show();
                Intent prefIntent = new Intent(this, MyPreferencesActivity.class);
                startActivity(prefIntent);
                return true;
            case R.id.menu_about:
                //Toast.makeText(this, "ADD!", Toast.LENGTH_SHORT).show();
                Intent aboutIntent = new Intent(this, MyAboutActivity.class);
                startActivity(aboutIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

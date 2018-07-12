package com.example.android.catimages;

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
    private String imageUrl = "http://24.media.tumblr.com/tumblr_m3dr9lfmr81r73wdao1_500.jpg";
    private ArrayList<String> imageUrls = new ArrayList<String>();
    private static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //loadImagesIntoViews("http://24.media.tumblr.com/tumblr_m3dr9lfmr81r73wdao1_500.jpg");
        getNewImages();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNewImages();
            }
        });
    }

    private void getNewImages () {
        requestQueue.add(stringRequest);
    }

    private void loadImagesIntoViews (String url) {
        ImageView imageView = (ImageView) findViewById(R.id.images);
        GlideApp.with(this).load(url).into(imageView);
    }

    StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d("mytesttag", "Response is: "+ response);

                    //Parsers.xpathParseXMLForTag(response);
                    ArrayList<String> parsedImageUrl = parseXMLForTag(response, "url");
                    Log.d("mytesttag", "Image URL is: "+ parsedImageUrl);
                    imageUrls = parsedImageUrl;
                    loadImagesIntoViews(imageUrls.get(0));
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("mytesttag", "That didn't work!");
        }
    });
}

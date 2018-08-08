package com.example.android.catimages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

public class SingleImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_image);
        Intent intent = getIntent();
        String intentimageurl = intent.getStringExtra("com.example.android.catimages.imageurl");
        ImageView imageView = findViewById(R.id.singleimage);
        GlideApp.with(this)
                .load(intentimageurl)
                .into(imageView);
        TextView textView = findViewById(R.id.source_backlink);
        textView.append(" " + intentimageurl);
    }
}

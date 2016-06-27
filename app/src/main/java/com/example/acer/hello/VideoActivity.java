package com.example.acer.hello;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by abceq on 2016/6/27.
 */
public class VideoActivity extends AppCompatActivity{

    View VideoView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        VideoView = getLayoutInflater().inflate(R.layout.video_main,null);
        setContentView(VideoView);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.ic_launcher);
    }
}

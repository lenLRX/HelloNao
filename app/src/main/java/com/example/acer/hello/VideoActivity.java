package com.example.acer.hello;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by abceq on 2016/6/27.
 */
public class VideoActivity extends AppCompatActivity{

    View VideoView = null;
    Button mReturnButton = null;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        VideoView = getLayoutInflater().inflate(R.layout.video_main,null);
        setContentView(VideoView);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.ic_launcher);
        mReturnButton = (Button) findViewById(R.id.return_to_main);
        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("fuck","shit");
                intent.setClass(VideoActivity.this , MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                VideoActivity.this.startActivity(intent);
                //VideoActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println(this.getLocalClassName()+"  resumed");
    }

    @Override
    protected void onPause(){
        super.onPause();
        System.out.println(this.getLocalClassName()+"  paused");
    }


    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
    }

}

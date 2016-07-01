package com.example.acer.hello;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by abceq on 2016/6/27.
 */
public class VideoActivity extends AppCompatActivity{

    public View VideoView = null;
    public Button mReturnButton = null;
    public Button mPlayVideoButton = null;
    public Handler imageViewHandler = null;
    public ImageView imageView = null;

    public Boolean videoRunning = false;

    public Thread videoThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        VideoView = getLayoutInflater().inflate(R.layout.video_main,null);
        setContentView(VideoView);
        imageView = (ImageView) findViewById(R.id.imageView);
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

        imageViewHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                imageView.setImageBitmap((Bitmap)msg.obj);
            }
        };

        mPlayVideoButton = (Button) findViewById(R.id.playVideo);

        mPlayVideoButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                boolean isRunning = false;
                synchronized (videoRunning){
                    isRunning = videoRunning;
                }
                if(!isRunning){
                    videoRunning = true;
                    GetImage getImage = new GetImage(null,imageViewHandler);
                    videoThread = new Thread(getImage);
                    videoThread.start();
                    mPlayVideoButton.setText("停止播放");
                }
                else{
                    videoRunning = false;
                    videoThread.interrupt();
                    mPlayVideoButton.setText("播放");
                }

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

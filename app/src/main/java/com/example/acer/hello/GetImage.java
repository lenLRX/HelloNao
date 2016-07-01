package com.example.acer.hello;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.aldebaran.qi.AnyObject;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.Session;

import java.util.Date;
import java.util.List;
import java.nio.ByteBuffer;

public class GetImage implements Runnable{
    private Handler textViewHandler = null;
    private Handler imageViewHandler = null;
    private Naoqi naoqi = null;
    private boolean shouldStop = false;
    private Object objLock = new Object();
    private AnyObject video = null;
    private String DeviceID;
    private final int width = 320;
    private final int height = 240;


    public GetImage(@Nullable Handler _textViewHandler,@Nullable Handler _imageViewHandler){
        textViewHandler = _textViewHandler;
        imageViewHandler = _imageViewHandler;
        try {
            naoqi = Naoqi.getInstance();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private void _sendMessageToTextView(String str){
        Message msg = new Message();
        msg.obj = str;
        textViewHandler.sendMessage(msg);
    }

    private void _sendMessageToImageView(Bitmap bmp){
        Message msg = new Message();
        msg.obj = bmp;
        imageViewHandler.sendMessage(msg);
    }



    private void innerLoop() throws Exception{
        if(Thread.interrupted())
            throw new Exception("interruped");
        Future<List<Object>> FutureImage = video.<List<Object>>call("getImageRemote",DeviceID);
        FutureImage.sync();
        List<Object> ImageList = FutureImage.get();
        ByteBuffer byteBuffer = (ByteBuffer) ImageList.get(6);
        byte[] imgRawBuffer = byteBuffer.array();
        byte[] ARBG8888 = Utillity.RBG888ToARBG8888(imgRawBuffer);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ByteBuffer ARBGbuffer = ByteBuffer.wrap(ARBG8888);
        bitmap.copyPixelsFromBuffer(ARBGbuffer);
        _sendMessageToImageView(bitmap);
    }


    @Override
    public void run(){
        try {
            Session session = naoqi.getSession();
            video = session.service("ALVideoDevice");
            //_sendMessageToTextView("ALVideoDevice connected\n");
            Future<String> futureDeviceID = video.<String>call("subscribeCamera", "dut", 0, 1, 11, 30);
            DeviceID = futureDeviceID.get();
            //_sendMessageToTextView("subscribeCamera DeviceID:" + DeviceID + "\n");

            while (true){
                synchronized (objLock){
                    if (shouldStop)
                        break;
                }
                Date date = new Date();
                long t1 = date.getTime();
                innerLoop();
                Date date2 = new Date();
                long t2 = date2.getTime();
                Long dt = t2-t1;
                System.out.println("spend time : "+dt);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }
}
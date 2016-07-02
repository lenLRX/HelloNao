package com.example.acer.hello;

import android.os.Handler;
import android.os.Message;

import com.aldebaran.qi.AnyObject;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abceq on 2016/7/1.
 */
public class PostureManager {
    private Naoqi naoqi = null;

    private Thread thread = null;

    private PostureManagerThread postureManagerThread = null;

    private Handler postureManagerHandler = null;

    public enum Postures{
        Crouch,
        LyingBack,
        LyingBelly,
        Sit,
        SitRelax,
        Stand,
        StandInit,
        StandZero
    }

    public static final Map<Postures,String> enumToString = new HashMap<Postures,String>(){
        {
            put(Postures.Crouch,"Crouch");
            put(Postures.LyingBack,"LyingBack");
            put(Postures.LyingBelly,"LyingBelly");
            put(Postures.Sit,"Sit");
            put(Postures.SitRelax,"SitRelax");
            put(Postures.Stand,"Stand");
            put(Postures.StandInit,"StandInit");
            put(Postures.StandZero,"StandZero");
        }
    };

    public static final float speed = 0.5f;

    public PostureManager(Naoqi naoqi){
        this.naoqi = naoqi;
    }

    public void Init(Handler postureManagerHandler){
        setPostureManagerHandler(postureManagerHandler);
        postureManagerThread = new PostureManagerThread(this);
        thread = new Thread(postureManagerThread);
        thread.start();
    }

    public Naoqi getNaoqi(){
        return naoqi;
    }

    public Handler getPostureManagerHandler(){
        return postureManagerHandler;
    }

    public void setPostureManagerHandler(Handler postureManagerHandler){
        this.postureManagerHandler = postureManagerHandler;
    }

    public void gotoPosture(String name){
        AnyObject naoqiPostureManager =
                postureManagerThread.getNaoqiPostureManager();
        try {
            naoqiPostureManager.call("goToPosture", name , speed);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}


class PostureManagerThread implements Runnable{
    private PostureManager postureManager = null;
    private AnyObject naoqiPostureManager = null;
    private Handler postureManagerHandler = null;

    public AnyObject getNaoqiPostureManager(){
        return naoqiPostureManager;
    }

    public PostureManagerThread(PostureManager postureManager){
        this.postureManager = postureManager;
    }
    @Override
    public void run(){
        try {
            Session session = postureManager.getNaoqi().getSession();
            System.out.println("ALRobotPosture Thread start");
            naoqiPostureManager = session.service("ALRobotPosture");
            System.out.println("ALRobotPosture Done");
            postureManagerHandler = postureManager.getPostureManagerHandler();

            while(true){
                Future<String> postureFuture = naoqiPostureManager.call("getPosture");
                postureFuture.sync();
                String posture = postureFuture.get();
                if(postureManagerHandler != null){
                    Message msg = new Message();
                    msg.obj = posture;
                    postureManagerHandler.sendMessage(msg);
                }

                Thread.sleep(500);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
package com.example.acer.hello;

import android.os.Handler;
import android.os.Message;

import com.aldebaran.qi.AnyObject;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.Session;

import java.util.List;

/**
 * Created by abceq on 2016/7/2.
 */
public class StiffnessManager {
    private Naoqi naoqi = null;

    private Thread thread = null;

    private StiffnessManagerThread stiffnessManagerThread = null;

    private Handler StiffnessManagerHandler = null;

    public StiffnessManager(Naoqi naoqi){
        this.naoqi = naoqi;
    }

    public void Init(Handler StiffnessManagerHandler){
        setStiffnessManagerHandler(StiffnessManagerHandler);
        naoqi = Naoqi.getInstance();
        stiffnessManagerThread = new StiffnessManagerThread(this);
        thread = new Thread(stiffnessManagerThread);
        thread.start();
    }

    public Naoqi getNaoqi() {
        return naoqi;
    }

    public Handler getStiffnessManagerHandler(){
        return StiffnessManagerHandler;
    }

    public void setStiffnessManagerHandler(Handler StiffnessManagerHandler){
        this.StiffnessManagerHandler = StiffnessManagerHandler;
    }

    public void interrupt(){
        thread.interrupt();
    }
}

class StiffnessManagerThread implements Runnable{
    private StiffnessManager stiffnessManager = null;
    private AnyObject naoqiALMotion = null;
    private Handler StiffnessManagerHandler = null;

    public StiffnessManagerThread(StiffnessManager stiffnessManager){
        this.stiffnessManager = stiffnessManager;
    }

    @Override
    public void run(){
        try {
            Session session = stiffnessManager.getNaoqi().getSession();
            System.out.println("ALRobotPosture Thread start");
            naoqiALMotion = session.service("ALRobotPosture");
            System.out.println("ALRobotPosture Done");
            StiffnessManagerHandler =
                    stiffnessManager.getStiffnessManagerHandler();

            while(true){
                Future<List<Float>> stiffnessesFuture = naoqiALMotion.call("getStiffnesses");
                stiffnessesFuture.sync();
                List<Float> stiffness = stiffnessesFuture.get();
                if(StiffnessManagerHandler != null){
                    Message msg = new Message();
                    msg.obj = stiffness.get(0);
                    StiffnessManagerHandler.sendMessage(msg);
                }

                Thread.sleep(500);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

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

    private Float stiffness = -1.0f;

    private boolean running = false;

    public boolean isRunning(){
        return running;
    }

    public StiffnessManager(Naoqi naoqi){
        this.naoqi = naoqi;
    }

    public void Init(Handler StiffnessManagerHandler){
        setStiffnessManagerHandler(StiffnessManagerHandler);
        naoqi = Naoqi.getInstance();
        stiffnessManagerThread = new StiffnessManagerThread(this);
        thread = new Thread(stiffnessManagerThread);
        thread.start();
        running = true;
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

    public void setStiffness(Float stiffness){
        this.stiffness = stiffness;
    }

    public Float getStiffness(){
        return stiffness;
    }

    public void wakeUp(){
        try {
            stiffnessManagerThread.getNaoqiALMotion().call("wakeUp");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void rest(){
        try {
            stiffnessManagerThread.getNaoqiALMotion().call("rest");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stiffnessUp(){
        try {
            stiffnessManagerThread.getNaoqiALMotion().call("setStiffnesses","Body",1.0f);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void interrupt(){
        thread.interrupt();
    }
}

class StiffnessManagerThread implements Runnable{
    private StiffnessManager stiffnessManager = null;
    private AnyObject naoqiALMotion = null;
    private Handler StiffnessManagerHandler = null;

    public AnyObject getNaoqiALMotion(){
        return naoqiALMotion;
    }

    public StiffnessManagerThread(StiffnessManager stiffnessManager){
        this.stiffnessManager = stiffnessManager;
    }

    @Override
    public void run(){
        try {
            Session session = stiffnessManager.getNaoqi().getSession();
            System.out.println("Stiffness Thread start");
            naoqiALMotion = session.service("ALMotion");
            System.out.println("Stiffness Done");
            StiffnessManagerHandler =
                    stiffnessManager.getStiffnessManagerHandler();

            while(true){
                Future<List<Float>> stiffnessesFuture = naoqiALMotion.call("getStiffnesses","Body");
                stiffnessesFuture.sync();
                List<Float> stiffness = stiffnessesFuture.get();
                if(StiffnessManagerHandler != null){
                    Message msg = new Message();
                    msg.obj = stiffness.get(0);
                    StiffnessManagerHandler.sendMessage(msg);
                }
                stiffnessManager.setStiffness(stiffness.get(0));
                Thread.sleep(500);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

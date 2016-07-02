package com.example.acer.hello;

import android.os.Handler;

import com.aldebaran.qi.AnyObject;

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
}

class StiffnessManagerThread implements Runnable{
    private StiffnessManager stiffnessManager = null;
    private AnyObject naoqiStiffnessManager = null;
    private Handler StiffnessManagerHandler = null;

    public StiffnessManagerThread(StiffnessManager stiffnessManager){
        this.stiffnessManager = stiffnessManager;
    }

    @Override
    public void run(){

    }
}

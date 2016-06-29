package com.example.acer.hello;

import android.os.Handler;
import android.os.Message;

import com.aldebaran.qi.AnyObject;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.Session;

import java.util.List;

/**
 * Created by abceq on 2016/6/26.
 */
public class BehaviorManager {
    private Naoqi naoqi = null;

    private Handler BehaviorTreeHandler = null;

    public Naoqi getNaoqi(){
        return naoqi;
    }

    private static BehaviorManager ourInstance = new BehaviorManager();

    public static BehaviorManager getInstance() {
        return ourInstance;
    }

    public void Init(Naoqi _naoqi){
        naoqi = _naoqi;
        new Thread(new BehaviorManagerThread(this)).start();
    }

    public Handler getBehaviorTreeHandler(){
        return BehaviorTreeHandler;
    }

    public void setBehaviorTreeHandler(Handler BehaviorTreeHandler){
        this.BehaviorTreeHandler = BehaviorTreeHandler;
    }

    private BehaviorManager() {
    }
}

class BehaviorManagerThread implements Runnable{
    private BehaviorManager mBehaviorManager = null;
    private AnyObject mNaoqiBehaviorManager = null;
    public BehaviorManagerThread(BehaviorManager _BehaviorManager){
        mBehaviorManager = _BehaviorManager;
    }

    @Override
    public void run(){
        Session session = mBehaviorManager.getNaoqi().getSession();
        try{
            System.out.println("Thread start");
            mNaoqiBehaviorManager = session.service("ALBehaviorManager");
            System.out.println("ALBehaviorManager Done");
            Future<List<String>> FInstalledBehaviors =
                    mNaoqiBehaviorManager.call("getInstalledBehaviors");
            System.out.println("called getInstalledBehaviors");
            FInstalledBehaviors.sync();
            List<String> InstalledBehaviors = FInstalledBehaviors.get();
            Handler BehaviorTreeHandler = mBehaviorManager.getBehaviorTreeHandler();
            if(BehaviorTreeHandler != null){
                Message msg = new Message();
                msg.obj = InstalledBehaviors;
                BehaviorTreeHandler.sendMessage(msg);
            }
            /*
            for(String behavior : InstalledBehaviors){
                System.out.println(behavior);
            }
            */
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
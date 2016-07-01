package com.example.acer.hello;

import android.os.Handler;
import android.os.Message;
import android.util.Pair;

import com.aldebaran.qi.AnyObject;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.Session;

import java.util.List;
import java.util.Map;

/**
 * Created by abceq on 2016/6/26.
 */
public class BehaviorManager {
    private Naoqi naoqi = null;

    private Handler BehaviorTreeHandler = null;

    private Handler runningBehaviorTextViewHandler = null;

    private TreeNodeRoot root = null;

    private BehaviorManagerThread behaviorManagerThread = null;

    public Naoqi getNaoqi(){
        return naoqi;
    }

    private static BehaviorManager ourInstance = new BehaviorManager();

    public static BehaviorManager getInstance() {
        return ourInstance;
    }

    public void Init(Naoqi _naoqi){
        naoqi = _naoqi;
        behaviorManagerThread = new BehaviorManagerThread(this);
        new Thread(behaviorManagerThread).start();
    }

    public void setRoot(TreeNodeRoot root) {
        this.root = root;
    }

    public TreeNodeRoot getRoot() {
        return root;
    }

    public Handler getRunningBehaviorTextViewHandler(){
        return runningBehaviorTextViewHandler;
    }

    public void setRunningBehaviorTextViewHandler(Handler handler){
        runningBehaviorTextViewHandler = handler;
    }

    public Handler getBehaviorTreeHandler(){
        return BehaviorTreeHandler;
    }

    public void setBehaviorTreeHandler(Handler BehaviorTreeHandler){
        this.BehaviorTreeHandler = BehaviorTreeHandler;
    }

    public AnyObject getBehaviorManagerObject(){
        return behaviorManagerThread.getmNaoqiBehaviorManager();
    }

    private BehaviorManager() {
    }
}

class BehaviorManagerThread implements Runnable{
    private BehaviorManager mBehaviorManager = null;
    private AnyObject mNaoqiBehaviorManager = null;
    private List<String> lastResults = null;
    private Object notifyObject = new Object();
    public BehaviorManagerThread(BehaviorManager _BehaviorManager){
        mBehaviorManager = _BehaviorManager;
    }

    public AnyObject getmNaoqiBehaviorManager(){
        return mNaoqiBehaviorManager;
    }

    private void getRunningBehaviors(List<String> runningBehaviors){
        Message msg = new Message();
        String plainRunningBehaviors = "";
        for(String bstr:runningBehaviors){
            plainRunningBehaviors += (bstr + "\n");
        }
        msg.obj = plainRunningBehaviors;
        mBehaviorManager.getRunningBehaviorTextViewHandler().sendMessage(msg);
        lastResults = runningBehaviors;
    }

    @Override
    public void run(){
        try{
            Session session = mBehaviorManager.getNaoqi().getSession();
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

                String[] stringArray = new String[InstalledBehaviors.size()];

                Pair<List<BehaviorBean>,TreeNodeRoot> datas =
                        NodeTree.getBehaviorBeanList(InstalledBehaviors.toArray(stringArray));

                mBehaviorManager.setRoot(datas.second);

                List<Node> nodes = TreeHelper.getSortedNodes(datas.first, 0);
                List<Node> Allnodes = nodes;
                System.out.println("Allnodes.size()"+Allnodes.size());
                Pair<List<Node>,List<Node>> pair =
                        new Pair<List<Node>,List<Node>>(nodes,Allnodes );
                msg.obj = pair;
                BehaviorTreeHandler.sendMessage(msg);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        while (true){
            try {
                Future<List<String>> runningBehaviorsFuture =
                        mNaoqiBehaviorManager.call("getRunningBehaviors");
                runningBehaviorsFuture.sync();
                List<String> runningBehaviors = runningBehaviorsFuture.get();
                if(lastResults != null){
                    if(!lastResults.equals(runningBehaviors)){
                        getRunningBehaviors(runningBehaviors);
                    }
                }
                else{
                    getRunningBehaviors(runningBehaviors);
                }


                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        /*
        while (true){
            try {
                synchronized (notifyObject){
                    notifyObject.wait();
                }
            }
            catch (Exception e){
                e.printStackTrace();
                break;
            }
        }
        */
    }
}

class BehaviorTask implements Runnable {
    @Override
    public void run(){

    }
}
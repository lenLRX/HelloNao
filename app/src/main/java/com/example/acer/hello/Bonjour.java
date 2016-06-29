package com.example.acer.hello;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Message;

import java.net.InetAddress;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by abceq on 2016/6/26.
 */
public class Bonjour {
    private NsdManager mNsdManager = null;
    private BonjourThread mBonjourThread = null;
    private Thread mThread = null;
    private Handler mDeviceSpinnerHandler = null;
    private Handler mTextViewHandler = null;
    private Set mDeviceSet = null;

    public NsdManager getmNsdManager(){
        return mNsdManager;
    }

    public BonjourThread getmBonjourThread(){
        return mBonjourThread;
    }

    public Thread getmThread(){
        return mThread;
    }

    public Handler getmTextViewHandler(){
        return mTextViewHandler;
    }

    public Handler getmDeviceSpinnerHandler(){
        return mDeviceSpinnerHandler;
    }

    public Set getmDeviceSet(){
        return mDeviceSet;
    }

    private static Bonjour ourInstance = new Bonjour();

    private static boolean Inited = false;

    public static Bonjour getInstance() {
        return ourInstance;
    }

    public void Init(Context ctx,Handler textViewHandler,Handler deviceSpinnerHandler,Set deviceSet){
        mNsdManager = (NsdManager) ctx.getSystemService(Context.NSD_SERVICE);
        mBonjourThread = new BonjourThread(this);
        mThread = new Thread(mBonjourThread);
        mDeviceSet = deviceSet;
        mTextViewHandler = textViewHandler;
        mDeviceSpinnerHandler = deviceSpinnerHandler;

        System.out.println("Bonjour Inited");
        mThread.start();
    }

    private Bonjour() {
    }
}

class BonjourThread implements Runnable {
    private Bonjour mBonjour = null;
    private NsdManager.DiscoveryListener mDiscoveryListener = null;
    private NsdManager.ResolveListener mResolveListener = null;
    private Object mNotifyObj = new Object();
    private String protocol = "_nao._tcp";
    private Queue mServiceQueue = new ArrayBlockingQueue(10);
    public BonjourThread(Bonjour bonjour){
        mBonjour = bonjour;
    }

    @Override
    public void run(){
        mDiscoveryListener = getmDiscoveryListener();
        mBonjour.getmNsdManager().discoverServices(
                protocol, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        System.out.println("Bonjour Thread started");
        while(true) {
            NsdServiceInfo _service = (NsdServiceInfo) mServiceQueue.poll();
            if(_service != null){
                System.out.println(_service.getServiceName());
                mBonjour.getmNsdManager().resolveService(_service, getmResolveListener());
                synchronized (mNotifyObj){
                    try {
                        mNotifyObj.wait();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                }
            }
            else{
                try {
                    //System.out.println("sleeping");
                    Thread.sleep(1000, 0);
                }
                catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private NsdManager.DiscoveryListener getmDiscoveryListener(){
        return new NsdManager.DiscoveryListener(){

            @Override
            public void onDiscoveryStarted(String regType) {
                System.out.println("Service discovery started");
            }

            @Override
            synchronized public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                System.out.println("Service discovery success " + service);
                /*
                Message msg = new Message();
                msg.obj = service.getServiceType() + service.getServiceName();
                mBonjour.getmTextViewHandler().sendMessage(msg);
                */

                mBonjour.getmDeviceSet().add(service.getServiceName());

                try {
                    mServiceQueue.add(service);
                }
                catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                System.out.println("service lost" + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                System.out.println("Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                System.out.println("onStartDiscoveryFailed: Error code: " + errorCode);
                if(NsdManager.FAILURE_ALREADY_ACTIVE != errorCode)
                    mBonjour.getmNsdManager().stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                System.out.println("onStartDiscoveryFailed: Error code: " + errorCode);
                mBonjour.getmNsdManager().stopServiceDiscovery(this);
            }
            public void finalize() throws Throwable{
                super.finalize();
                System.out.println("stopping");
                mBonjour.getmNsdManager().stopServiceDiscovery(this);
            }
        };
    }

    private NsdManager.ResolveListener getmResolveListener(){
        return new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails.  Use the error code to debug.
                System.out.println("Resolve failed " + errorCode);
            }

            @Override
            synchronized public void onServiceResolved(NsdServiceInfo serviceInfo) {
                System.out.println("Resolve Succeeded. " + serviceInfo);
                int port = serviceInfo.getPort();
                InetAddress host = serviceInfo.getHost();

                Message msg = new Message();
                msg.obj = serviceInfo.getServiceName()+" @ " + host.toString() + ":"+port;
                mBonjour.getmDeviceSpinnerHandler().sendMessage(msg);

                try {
                    synchronized (mNotifyObj){
                        //System.out.println("before notify");
                        mNotifyObj.notify();
                        //System.out.println("after notify");
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
        };
    }
}
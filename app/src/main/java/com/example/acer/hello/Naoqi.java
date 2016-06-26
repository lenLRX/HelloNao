package com.example.acer.hello;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.Session;

public class Naoqi {
    private static Naoqi instance = null;

    private String _IP;
    private Application app;


    private Naoqi(){};

    private void init(String IP){
        String[] args = new String[2];
        args[0] = "--qi-url";
        args[1] = IP;
        _IP = IP;

        app = new Application(args);
        app.start();
    }

    public Session getSession(){
        return app.session();
    }

    public String get_IP(){
        return _IP;
    }

    public synchronized static Naoqi getInstance(String IP) throws Exception{
        if(null == instance){
            instance = new Naoqi();
            instance.init(IP);
        }
        else{
            if(!instance._IP.equals(IP))
                throw new Exception("IP is not equal to the IP we connected!");
        }
        return instance;
    }
}
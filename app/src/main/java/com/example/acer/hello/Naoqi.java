package com.example.acer.hello;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.Session;

public class Naoqi {
    private static Naoqi instance = new Naoqi();

    private String _IP;
    private Application app;

    private boolean running = false;

    public boolean isRunning(){
        return running;
    }

    private Naoqi(){};

    public void init(String IP){
        try {
            String[] args = new String[2];
            args[0] = "--qi-url";
            args[1] = IP;
            _IP = IP;

            app = new Application(args);
            app.start();
            running = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stop(){
        running = false;
        app.stop();
    }

    public Session getSession() throws Exception {
        if(!running)
            throw new Exception("disconnected");
        return app.session();
    }

    public String get_IP(){
        return _IP;
    }

    public synchronized static Naoqi getInstance() {
        return instance;
    }
}
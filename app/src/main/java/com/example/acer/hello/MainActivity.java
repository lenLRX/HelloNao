package com.example.acer.hello;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.Color;

import com.aldebaran.qi.AnyObject;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.Application;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public String linkStat = "unconnected";
    public Button connectButton = null;
    public TextView text = null;
    public ImageView imageView = null;
    public Spinner deviceSpinner = null;
    public ArrayAdapter<String> deviceSpinnerAdapter = null;
    public Handler textViewHandler = null;
    public Handler imageViewHandler = null;
    public Handler deviceSpinnerHandler = null;
    public EditText ipTextfield = null;
    public boolean imgRunning = false;
    public Object objLock = new Object();
    public Set deviceSet = new HashSet();
    public ArrayList<String> deviceNames = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext());

        text = (TextView) findViewById(R.id.mainText);

        ipTextfield = (EditText) findViewById(R.id.ipInputBox);

        imageView = (ImageView)findViewById(R.id.imageView);

        imageView.setVisibility(View.VISIBLE);

        text.setTextColor(Color.rgb(0,0,0));

        deviceSpinner = (Spinner) findViewById(R.id.deviceSpinner);
        deviceNames = new ArrayList();
        deviceNames.add("none");
        deviceSpinnerAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, deviceNames);
        deviceSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        deviceSpinner.setAdapter(deviceSpinnerAdapter);
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("onItemSelected");
                int backSplashPos = 0;
                int endPosOfIP = 0;
                String selected = " ";
                try {
                    selected = deviceNames.get(position);
                    backSplashPos = selected.indexOf("/");
                    endPosOfIP = selected.indexOf(":");

                    if(backSplashPos > 0 && endPosOfIP > backSplashPos){
                        String _IP = selected.substring(backSplashPos + 1, endPosOfIP);
                        ipTextfield.setText(_IP);
                        System.out.println("IP: " + _IP);
                    }
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }

                System.out.println(selected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("onNothingSelected");
            }
        });
        connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                synchronized (objLock){
                    if(imgRunning)
                        return;
                }

                imgRunning = true;

                Editable editableText;
                try {
                    editableText = ipTextfield.getText();
                }
                catch (NullPointerException e){
                    return;
                }
                String ip = editableText.toString();
                if(Utillity.isIPValid(ip)){
                    //GetImage getImage = new GetImage(textViewHandler,imageViewHandler,"tcp://"+ip+":9559");
                    //new Thread(getImage).start();
                    try {
                        BehaviorManager.getInstance().Init(Naoqi.getInstance("tcp://" + ip + ":9559"));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                }
                else{
                    Message msg = new Message();
                    msg.obj = ip + "is invalid\n";
                    textViewHandler.sendMessage(msg);
                }
            }
        });

        textViewHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                text.setText((String) msg.obj);
            }
        };

        imageViewHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                imageView.setImageBitmap((Bitmap)msg.obj);
            }
        };

        deviceSpinnerHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                deviceNames.add((String) msg.obj);
            }
        };

        Bonjour.getInstance().Init(getApplicationContext(),textViewHandler,
                deviceSpinnerHandler,deviceSet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}




class MyThread implements Runnable {

    Handler mainActivityHandler = null;

    public  MyThread(Handler _mainActivityHandler){
        mainActivityHandler = _mainActivityHandler;
    }

    private void _sendMessage(String str){
        Message msg = new Message();
        msg.obj = str;
        mainActivityHandler.sendMessage(msg);
    }

    @Override
    public void run() {
        _sendMessage("Thread started\n");

        String[] args = new String[2];
        args[0] = "--qi-url";
        args[1] = "tcp://192.168.1.112:9559";
        System.out.println("going to new App!!");
        System.out.flush();
        Application app = null;
        try {
            app = new Application(args);
            System.out.println("app success");
            _sendMessage("Create app success\n");
        }
        catch (Exception e){
            _sendMessage(e.getMessage());
            System.out.println(e.getMessage());
            System.out.flush();
        }

        try{
            app.start();
            _sendMessage("app successfully started\n");
            System.out.println("app successfully started\n");
        }
        catch (Exception e){
            _sendMessage(e.getMessage());
            System.out.println(e.getMessage());
            System.out.flush();
        }

        Session client = null;

        try{
            client = app.session();
            if(client.isConnected())
                System.out.println("connected");
            else
                System.out.println("unconnected");
        }
        catch (Exception e){
            _sendMessage(e.getMessage());
            System.out.println(e.getMessage());
            System.out.flush();
        }


        try{
            client.service("ALMemory");
            System.out.println("ALMemory success!! \n");
            _sendMessage("ALMemory success!! \n");
        }
        catch (Exception e){
            _sendMessage(e.getMessage());
            System.out.println(e.getMessage());
            System.out.flush();
        }

        AnyObject motionProxy = null;

        try{
            motionProxy = client.service("ALMotion");
            System.out.println("ALMotion connected\n");
            _sendMessage("ALMotion connected\n");
        }
        catch (Exception e){
            _sendMessage(e.getMessage());
            System.out.println(e.getMessage());
            System.out.flush();
        }

        try{
            motionProxy.call("setStiffnesses", "Body", 1.0f).sync();
            System.out.println("setStiffneesses done\n");
            _sendMessage("setStiffneesses done\n");
        }
        catch (Exception e){
            _sendMessage(e.getMessage());
            System.out.println(e.getMessage());
            System.out.flush();
        }

        try{
            _sendMessage("Ready to move\n");
            motionProxy.call("moveTo", 10.0f, 0.0f,2.0f).sync();
            System.out.println("move done\n");
            _sendMessage("move done\n");
        }
        catch (Exception e){
            _sendMessage(e.getMessage());
            System.out.println(e.getMessage());
            System.out.flush();
        }


    }
}

class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler instance;  //单例引用，这里我们做成单例的，因为我们一个应用程序里面只需要一个UncaughtExceptionHandler实例

    private CrashHandler(){}

    public synchronized static CrashHandler getInstance(){  //同步方法，以免单例多线程环境下出现异常
        if (instance == null){
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx){  //初始化，把当前对象设置成UncaughtExceptionHandler处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {  //当有未处理的异常发生时，就会来到这里。。
        System.out.println("uncaughtException, thread: " + thread
                + " name: " + thread.getName() + " id: " + thread.getId() + "exception: "
                + ex);
        ex.printStackTrace();
        String threadName = thread.getName();
    }

}
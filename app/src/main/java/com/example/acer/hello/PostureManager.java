package com.example.acer.hello;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abceq on 2016/7/1.
 */
public class PostureManager {
    private Naoqi naoqi = null;

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

    public static Map<Postures,String> enumToString = new HashMap<Postures,String>(){
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

    public PostureManager(Naoqi naoqi){
        this.naoqi = naoqi;
    }
}

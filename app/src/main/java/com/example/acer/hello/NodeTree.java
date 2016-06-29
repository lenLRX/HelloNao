package com.example.acer.hello;



import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abceq on 2016/6/29.
 */
public class NodeTree {
    private String name;
    private NodeTree parent = null;
    private Map<String,NodeTree> children = new HashMap<>();
    private int uid = 0;

    public static List<BehaviorBean> getBehaviorBeanList(String[] Behaviors){
        List<BehaviorBean> mData = new ArrayList<>();
        Map<String,NodeTree> root = new HashMap<>();

        for(String str : Behaviors){
            String[] splited = str.split("/");
            //System.out.println(splited.length);
            NodeTree currNode = null;
            for(String segment : splited){
                if(!segment.isEmpty()){
                    //System.out.println(segment);
                    if(currNode == null){

                        if (root.containsKey(segment)){
                            currNode = root.get(segment);
                        }
                        else{
                            currNode = new NodeTree(segment,null);
                            root.put(segment, currNode);

                        }
                    }
                    else{
                        if(currNode.contains(segment)){
                            currNode = currNode.getChild(segment);
                        }
                        else{
                            currNode = currNode.addChild(segment);
                        }
                    }

                }//segment no empty
            }//for segment
        }//for str

        //System.out.println("**************************");

        List<BehaviorBean> behaviorBeenList = new ArrayList<BehaviorBean>();

        for(Map.Entry<String,NodeTree> entry : root.entrySet()){
            entry.getValue().traverse(behaviorBeenList);
        }

        return behaviorBeenList;
    }

    public NodeTree(String name , @Nullable NodeTree parent){
        this.name = name;
        this.parent = parent;
        uid = uidGenerator.getInstance().getUid();
    }

    public boolean contains(String key){
        return children.containsKey(key);
    }

    public NodeTree getChild(String key){
        return children.get(key);
    }

    public NodeTree addChild(String name){
        NodeTree child = new NodeTree(name,this);
        children.put(name,child);
        return child;
    }

    public Map<String,NodeTree> getChildren(){
        return children;
    }

    public void traverse(int indent){
        String indentStr = "    ";
        String iStr = "";
        for(int i = 0 ; i < indent ;i++){
            iStr = iStr + indentStr;
        }
        System.out.println(iStr + "<" + name);
        for(Map.Entry<String, NodeTree> childEntery : children.entrySet()){
            childEntery.getValue().traverse(indent +1);
        }
        //System.out.println(iStr + name + ">");
    }

    public void traverse(@NotNull List<BehaviorBean> behaviorBeanList){
        behaviorBeanList.add(new BehaviorBean(uid,
                parent != null ? parent.getUid() : 0,name));
        for(Map.Entry<String, NodeTree> childEntery : children.entrySet()){
            childEntery.getValue().traverse(behaviorBeanList);
        }
    }

    public int getUid(){
        return uid;
    }
}


class uidGenerator{
    private int uid = 0;

    private static uidGenerator theInstance = new uidGenerator();

    public static uidGenerator getInstance(){
        return theInstance;
    }

    public int getUid(){
        uid++;
        return uid;
    }
}
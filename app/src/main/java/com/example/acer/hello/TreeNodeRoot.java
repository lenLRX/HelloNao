package com.example.acer.hello;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by abceq on 2016/6/29.
 */
public class TreeNodeRoot {
    private Map<String,NodeTree> root = new HashMap<String,NodeTree>();

    public boolean contains(String key){
        return root.containsKey(key);
    }

    public void put(String key, NodeTree nodeTree){
        root.put(key,nodeTree);
    }

    public NodeTree get(String key){
        return root.get(key);
    }

    public Set<Map.Entry<String,NodeTree>> getEntrySet(){
        return root.entrySet();
    }

    public String traverseAndGetFullName(String lastName){
        String FullName = "";
        for (Map.Entry<String,NodeTree> subRoot : root.entrySet()){
            NodeTree leaf = subRoot.getValue().traverse(lastName);
            if(leaf != null){
                try {
                    FullName = leaf.backTraceGetFullName();
                    FullName = FullName.substring(1,FullName.length());
                }
                catch (Exception e){
                    e.printStackTrace();
                    System.err.println(e.getMessage());
                }
            }
        }
        return FullName;
    }
}

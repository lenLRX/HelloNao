package com.example.acer.hello;

/**
 * Created by abceq on 2016/6/26.
 */
public class BehaviorBean {
    @TreeNodeId
    private int _id;
    @TreeNodePid
    private int parentId;
    @TreeNodeLabel
    private String name;
    private long length;
    private String desc;

    public BehaviorBean(int _id, int parentId, String name)
    {
        super();
        this._id = _id;
        this.parentId = parentId;
        this.name = name;
    }
}

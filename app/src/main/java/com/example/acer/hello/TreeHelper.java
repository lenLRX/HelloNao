package com.example.acer.hello;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abceq on 2016/6/26.
 */
public class TreeHelper {
    public static <T> List<Node> getSortedNodes(List<T> datas,
                                                int defaultExpandLevel) throws IllegalArgumentException,
            IllegalAccessException

    {
        List<Node> result = new ArrayList<Node>();
        //将用户数据转化为List<Node>以及设置Node间关系
        List<Node> nodes = convertData2Node(datas);
        //拿到根节点

        List<Node> rootNodes = getRootNodes(nodes);
        //排序
        for (Node node : rootNodes)
        {
            addNode(result, node, defaultExpandLevel, 1);
        }

        return result;
    }

    public static List<Node> filterVisibleNode(List<Node> nodes)
    {
        List<Node> result = new ArrayList<Node>();

        for (Node node : nodes)
        {
            // 如果为跟节点，或者上层目录为展开状态
            if (node.isRoot() || node.isParentExpand())
            {
                setNodeIcon(node);
                result.add(node);
            }
        }
        return result;
    }

    private static <T> List<Node> convertData2Node(List<T> datas)
            throws IllegalArgumentException, IllegalAccessException

    {
        List<Node> nodes = new ArrayList<Node>();
        Node node = null;

        for (T t : datas)
        {
            int id = -1;
            int pId = -1;
            String label = null;
            Class<? extends Object> clazz = t.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field f : declaredFields)
            {
                if (f.getAnnotation(TreeNodeId.class) != null)
                {
                    f.setAccessible(true);
                    id = f.getInt(t);
                }
                if (f.getAnnotation(TreeNodePid.class) != null)
                {
                    f.setAccessible(true);
                    pId = f.getInt(t);
                }
                if (f.getAnnotation(TreeNodeLabel.class) != null)
                {
                    f.setAccessible(true);
                    label = (String) f.get(t);
                }
                if (id != -1 && pId != -1 && label != null)
                {
                    break;
                }
            }
            node = new Node(id, pId, label);
            nodes.add(node);
        }

        Map<Integer,Node> nodeMap = new HashMap<Integer,Node>();

        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            nodeMap.put(i,n);
        }

        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            int pid = n.getpId();
            if(pid > 0){
                Node parent = nodeMap.get(pid);
                n.setParent(parent);
                parent.getChildren().add(n);
            }

        }

        /**
         * 设置Node间，父子关系;让每两个节点都比较一次，即可设置其中的关系
         * 这样排序，复杂度太高
         */
        /*
        for (int i = 0; i < nodes.size(); i++)
        {
            Node n = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++)
            {
                Node m = nodes.get(j);
                if (m.getpId() == n.getId())
                {
                    n.getChildren().add(m);
                    m.setParent(n);
                } else if (m.getId() == n.getpId())
                {
                    m.getChildren().add(n);
                    n.setParent(m);
                }
            }
        }
        */
        // 设置图片
        for (Node n : nodes)
        {
            setNodeIcon(n);
        }
        return nodes;
    }

    private static List<Node> getRootNodes(List<Node> nodes)
    {
        List<Node> root = new ArrayList<Node>();
        for (Node node : nodes)
        {
            if (node.isRoot())
                root.add(node);
        }
        return root;
    }

    /**
     * 把一个节点上的所有的内容都挂上去
     */
    private static void addNode(List<Node> nodes, Node node,
                                int defaultExpandLeval, int currentLevel)
    {

        nodes.add(node);
        if (defaultExpandLeval >= currentLevel)
        {
            node.setExpand(true);
        }

        if (node.isLeaf())
            return;
        for (int i = 0; i < node.getChildren().size(); i++)
        {
            addNode(nodes, node.getChildren().get(i), defaultExpandLeval,
                    currentLevel + 1);
        }
    }

    /**
     * 设置节点的图标
     *
     * @param node
     */
    private static void setNodeIcon(Node node)
    {
        if (node.getChildren().size() > 0 && node.isExpand())
        {
            node.setIcon(R.drawable.tree_ex);
        } else if (node.getChildren().size() > 0 && !node.isExpand())
        {
            node.setIcon(R.drawable.tree_ec);
        } else
            node.setIcon(-1);

    }
}

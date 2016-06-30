package com.example.acer.hello;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by abceq on 2016/6/26.
 */
public class TreeAdapter<T> extends TreeListViewAdapter<T>{

    public TreeAdapter(ListView mTree, Context context,List<Node> mNodes,
                       List<Node> mAllNodes,int defaultExpandLevel) throws IllegalArgumentException,
            IllegalAccessException
    {
        super(mTree, context, mNodes, mAllNodes, defaultExpandLevel);
    }

    @Override
    public View getConvertView(Node node , int position, View convertView, ViewGroup parent)
    {

        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.tree_list_view, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView
                    .findViewById(R.id.id_treenode_icon);
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.label = (TextView) convertView
                    .findViewById(R.id.id_treenode_label);
            viewHolder.label.setVisibility(View.VISIBLE);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (node.getIcon() == -1)
        {
            viewHolder.icon.setVisibility(View.INVISIBLE);
        } else
        {
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageResource(node.getIcon());
        }
        viewHolder.label.setText(node.getName());

        return convertView;
    }

    private final class ViewHolder
    {
        ImageView icon;
        TextView label;
    }

}


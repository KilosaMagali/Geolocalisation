package com.manhattanproject.geolocalisation;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kilosakeyrocker on 22/02/15.
 */
public class AdapterListLieu extends BaseExpandableListAdapter {
    private Context context;
    private HashMap<String, List<String>> lieu_container;
    private List<String> lieu_list;
    private long position=0;

    public AdapterListLieu(Context context, HashMap<String, List<String>> lieu_container, List<String> lieu_list )
    {
        this.context = context;
        this.lieu_container = lieu_container;
        this.lieu_list = lieu_list;

    }

    @Override
    public int getGroupCount() {
        return lieu_list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return lieu_container.get(lieu_list.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return lieu_list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return lieu_container.get(lieu_list.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle= (String) getGroup(groupPosition);
        if(convertView==null){
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.list_item, parent,false);
        }
        TextView lieuTextview= (TextView) convertView.findViewById(R.id.parent_lieu);
        lieuTextview.setTypeface(null, Typeface.BOLD);
        lieuTextview.setText(groupTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childTitle=(String)getChild(groupPosition,childPosition);
        if(convertView==null){
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.lieu_detail, parent,false);
            TextView itemTextView = (TextView) convertView.findViewById(R.id.child_lieu);
            itemTextView.setText(childTitle);

        }
        else {

                TextView itemTextView = (TextView) convertView.findViewById(R.id.child_lieu);
                itemTextView.setText(childTitle);
            }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

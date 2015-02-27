package com.manhattanproject.geolocalisation;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Vincent on 22/02/15.
 */
public class AdapterListAmi extends BaseExpandableListAdapter {
    private Context context;
    private List<Ami> Ami_list;

    public AdapterListAmi(Context context, List<Ami> Ami_list )
    {
        this.context = context;
        this.Ami_list = Ami_list;

    }

    @Override
    public int getGroupCount() {
        return Ami_list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 3;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return Ami_list.get(groupPosition);
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        switch(childPosition){
            case 0:
                return Ami_list.get(groupPosition).getStatut();
            case 1:
                return Ami_list.get(groupPosition).getPosition();
            default:
                if (Ami_list.get(groupPosition).isConnect())
                    return Ami_list.get(groupPosition).getPseudo()+" est en ligne";
                else
                    return Ami_list.get(groupPosition).getPseudo()+" est hors ligne";

        }
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
        String groupTitle= ((Ami)getGroup(groupPosition)).getPseudo();
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
        String childTitle = getChild(groupPosition,childPosition).toString();
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


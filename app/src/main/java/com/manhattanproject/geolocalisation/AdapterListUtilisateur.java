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
public class AdapterListUtilisateur extends BaseExpandableListAdapter {
    private Context context;
    private List<Utilisateur> User_list;

    public AdapterListUtilisateur(Context context, List<Utilisateur> User_list )
    {
        this.context = context;
        this.User_list = User_list;

    }

    @Override
    public int getGroupCount() {
        return User_list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 2;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return User_list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        switch(childPosition){
            case 0:
                return User_list.get(groupPosition).getStatut();

            default:
                return User_list.get(groupPosition).getPosition();
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
        String groupTitle= ((Utilisateur)getGroup(groupPosition)).getPseudo();
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
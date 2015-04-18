package com.manhattanproject.geolocalisation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vince_000 on 18/04/2015.
 */
public class AdapterListAmiPartage extends BaseAdapter{
    List<Ami> person;
    LayoutInflater inflater;

    public AdapterListAmiPartage (Context context, List<Ami> person)
    {
        inflater = LayoutInflater.from (context);
        this.person = person;
    }

    @Override
    public int getCount ()
    {
        return person.size ();
    }

    @Override
    public Object getItem (int position)
    {
        return person.get (position);
    }

    @Override
    public long getItemId (int position)
    {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            holder = new ViewHolder ();
            convertView = inflater.inflate (R.layout.affichageitem, null);
            holder.pseudo = (TextView) convertView.findViewById (R.id.pseudo);
            holder.checked = (CheckBox) convertView
                    .findViewById (R.id.checkbox);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag ();
        }
        holder.position = person.get (position).getPos();
        holder.pseudo.setText(person.get(position).getPseudo());
        holder.checked.setChecked (person.get (position).isChecked ());
        holder.checked.setClickable (false);
        holder.checked.setEnabled (true);
        holder.checked.setTag (holder);
        convertView.setTag (holder);
        return convertView;
    }
    //Classe permettant de sauvegarder l'etat de la personne et de pouvoir recuperer la position.
    public class ViewHolder
    {
        TextView	pseudo;
        CheckBox	checked;
        int			position;
    }
}

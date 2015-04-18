package com.manhattanproject.geolocalisation;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CheckActivity extends ListActivity
{
    /** Called when the activity is first created. */

    private EditText _filterText		= null;
    private AdapterListAmiPartage _adapter		= null;
    private List<Ami> person;
    private ListView personList;

    @SuppressWarnings("unchecked")

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.checkactivity);

        Utilisateur courant = new Utilisateur();
        courant.recup(getApplicationContext());
        System.out.println(courant.getPseudo());
        person = new ArrayList<Ami>();
        System.out.println(person.size());
        Collections.sort(person);

        // Initialisation de la position
        for (int i = 0; i < person.size (); i++)
            person.get (i).setPos(i);

        _adapter = new AdapterListAmiPartage (this, person);
        setListAdapter (_adapter);
        personList = getListView ();
        personList.setItemsCanFocus (false);
        personList.setOnItemClickListener (new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick (AdapterView<?> parentView,
                                     View selectedItemView, int position, long id)
            {
                CheckBox c = (CheckBox) selectedItemView
                        .findViewById (R.id.checkbox);
//On recupere le tag
                AdapterListAmiPartage.ViewHolder view = ((AdapterListAmiPartage.ViewHolder) c.getTag ());
//On gere le check
                if (!c.isChecked()) {
                    c.setChecked(true);
                    person.get(view.position).setChecked(true);
                    Log.v("Checked ", "true" + position);

                } else {
                    c.setChecked(false);
                    person.get(view.position).setChecked(false);
                    Log.v("Checked", "false" + position);
                    System.out.println("green green potatoe");
                }
//On replace la liste à la bonne position
                int pos = parentView.getFirstVisiblePosition ();
                personList.setSelection (pos);
            }
        });
    }
    public void send(View view){
        CheckBox c;
        ArrayList<String> amisChecked= new ArrayList<String>();
        for(int i = 0;i < person.size();++i){
            if(person.get(i).isChecked())
                amisChecked.add(person.get(i).getPseudo());
        }
        Intent result = new Intent();
        result.putStringArrayListExtra("amis",amisChecked);
        setResult(RESULT_OK, result);
        finish();
    }

    public void retour(View view){
        Intent result = new Intent();
        result.putStringArrayListExtra("amis",null);
        setResult(RESULT_CANCELED,result );
        finish();
    }

    public void allSelect(View view){

    }
}

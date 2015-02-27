package com.manhattanproject.geolocalisation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ExpandableListView;

import java.util.ArrayList;


public class Activity_list_ami extends ActionBarActivity {
    private ExpandableListView expandableList;
    private AdapterListAmi adaptor;
    private ArrayList<Ami> listeAmi;
    private DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ami);
        expandableList = (ExpandableListView) findViewById(R.id.expandableListAmi);
        db = new DataBase(getApplicationContext(),"base de donne",null,2);
        listeAmi = db.recupAmiBD();
        System.out.println(listeAmi.get(0));
        adaptor = new AdapterListAmi(this, listeAmi);
        expandableList.setAdapter(adaptor);

    }
}

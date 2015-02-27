package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;

/**
 * Created by kilosakeyrocker on 22/02/15.
 */
public class Activity_list_lieu extends Activity{
    private ExpandableListView expandableList;
    private AdapterListLieu adaptor;
    private ArrayList<Lieu> listeLieu;
    private DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_lieu);
        expandableList = (ExpandableListView) findViewById(R.id.expandableListLieu);
        db = new DataBase(getApplicationContext(),"base de donne",null,2);
        listeLieu = db.recupLieuBD();
        adaptor = new AdapterListLieu(this, listeLieu);
        expandableList.setAdapter(adaptor);

    }
}

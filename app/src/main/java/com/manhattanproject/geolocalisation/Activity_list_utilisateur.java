package com.manhattanproject.geolocalisation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ExpandableListView;

import java.util.ArrayList;


public class Activity_list_utilisateur extends ActionBarActivity {
    private ExpandableListView expandableList;
    private AdapterListUtilisateur adaptor;
    private ArrayList<Utilisateur> listeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_utilisateur);
        expandableList = (ExpandableListView) findViewById(R.id.expandableListAmi);
        adaptor = new AdapterListUtilisateur(this, listeUser);
        expandableList.setAdapter(adaptor);

    }
}

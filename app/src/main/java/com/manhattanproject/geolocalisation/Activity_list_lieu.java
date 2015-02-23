package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kilosakeyrocker on 22/02/15.
 */
public class Activity_list_lieu extends Activity{
    private HashMap<String, List<String>> lieuContainer;
    private List<String> lieuList;
    private ExpandableListView expandableList;
    private AdapterListLieu adaptor;
    private ArrayList<String> ll1,ll2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_lieu);
        expandableList = (ExpandableListView) findViewById(R.id.expandableList);
        ll1=getIntent().getStringArrayListExtra("locationNames");
        ll2=getIntent().getStringArrayListExtra("locationCategories");
        lieuContainer = DataProvider.getInfo(ll1,ll2);
        lieuList = new ArrayList<>(lieuContainer.keySet());
        adaptor = new AdapterListLieu(this, lieuContainer, lieuList);
        expandableList.setAdapter(adaptor);

    }
}

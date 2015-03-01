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
        db = new DataBase(getApplicationContext(),"base de donne",null,4);
        listeAmi = db.recupAmiBD();
        //System.out.println(listeAmi.get(0));
        /*Utilisateur courant = new Utilisateur();
        courant.recup(getApplicationContext());
        final String[] params={"selectUsersFriends.php","pseudo","4rrrrrr"};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Requete r=new Requete();
                String response=r.LancerRequete(params);
                try{
                    JSONArray jArray = new JSONArray(response);
                    for(int i=0;i<jArray.length();i++){
                        JSONObject json_data = jArray.getJSONObject(i);
                        listeAmi.add(i,new Ami(i,null, json_data.getString("pseudo"), null));
                    }
                }catch(JSONException e){
                    Log.e("log_tag", "Error parsing data "+e.toString());
                }
            }
        });
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        adaptor = new AdapterListAmi(this, listeAmi);
        expandableList.setAdapter(adaptor);

    }
}

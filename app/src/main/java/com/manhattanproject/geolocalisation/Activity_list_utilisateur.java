package com.manhattanproject.geolocalisation;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ExpandableListView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Activity_list_utilisateur extends ActionBarActivity {
    private ExpandableListView expandableList;
    private AdapterListUtilisateur adaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_utilisateur);
        //new selectAll().execute(null,null,null);

    }

    private class selectAll extends AsyncTask<Void,Void,Void>{
        private ArrayList<Utilisateur> listeUser;
        @Override
        protected Void doInBackground(Void... params) {
            Utilisateur courant = new Utilisateur();
            courant.recup(getApplicationContext());
            final String[] p={"selectAllUsers.php","pseudo","4rrrrrr"};
            Requete r=new Requete();
            String response=r.LancerRequete(p);
            try{
                JSONArray jArray = new JSONArray(response);
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    listeUser.add(i,new Utilisateur(i, json_data.getString("pseudo"), null, null, null));
                }
            }catch(JSONException e){
                Log.e("log_tag", "Error parsing data " + e.toString());
            }
            expandableList = (ExpandableListView) findViewById(R.id.expandableListAmi);
            adaptor = new AdapterListUtilisateur(getApplicationContext(), listeUser);
            expandableList.setAdapter(adaptor);
            return null;
        }
    }
}

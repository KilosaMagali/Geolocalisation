package com.manhattanproject.geolocalisation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class Activity_Search extends ActionBarActivity implements AdapterView.OnItemSelectedListener{
    private ExpandableListView expandableList;
    private AdapterListUtilisateur adaptor;
    private ArrayList<Utilisateur> listeUser=new ArrayList<>();
    private EditText et;
    private Utilisateur uClicked;
    private String userSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__search);
        expandableList = (ExpandableListView) findViewById(R.id.expandableListUtilisateur);
        et=(EditText)findViewById(R.id.editText);
        et.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listeUser.clear();
                if(count!=0)
                    recherche(s.toString());
            }
        });
        registerForContextMenu(expandableList);
        adaptor = new AdapterListUtilisateur(this, listeUser);
        expandableList.setAdapter(adaptor);

    }
    protected void recherche(String pseudo){
        final String[] params={"selectUsersStartingBy.php","pseudo",pseudo};
        Requete r = new Requete();
        r.execute(params);
        try {
            r.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String response=r.getResult();
        try{
            JSONArray jArray = new JSONArray(response);
            System.out.println("Donnée de la réponse : "+jArray);
            for(int i=0;i<jArray.length();i++){
                JSONObject json_data = jArray.getJSONObject(i);
                System.out.println("Donnée de l'ami : "+json_data.getString("pseudo"));
                listeUser.add(i,new Utilisateur(i, json_data.getString("pseudo"), null, json_data.getString("statut"), new LatLng(json_data.getDouble("positionx"),json_data.getDouble("positiony"))));            }
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        adaptor.notifyDataSetChanged();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo
                info=(ExpandableListView.ExpandableListContextMenuInfo)menuInfo;
        if(ExpandableListView.getPackedPositionType(info.packedPosition)
                == ExpandableListView.PACKED_POSITION_TYPE_GROUP){
            uClicked=listeUser.get((int)info.id);
            menu.setHeaderTitle(uClicked.getPseudo());
            String [] menuItems=getResources().getStringArray(R.array.menuSearchLongClick);
            for(int i=0; i<menuItems.length; i++){
                menu.add(Menu.NONE,i,i,menuItems[i]);
            } }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex=item.getItemId();
        String [] menuItems=getResources().getStringArray(R.array.menuSearchLongClick);

        switch(menuItemIndex){
            case 0: //envoyer une demande
                String rid = null;
                final String[] params={"selectUser.php","pseudo",uClicked.getPseudo()};
                System.out.println(uClicked.getPseudo());
                Requete r = new Requete();
                r.execute(params);
                try {
                    r.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                String response=r.getResult();
                try{
                    JSONArray jArray = new JSONArray(response);
                    System.out.println("Donnée de la réponse : "+jArray);
                    JSONObject json_data = jArray.getJSONObject(0);
                    rid=json_data.getString("rid");
                    System.out.println(rid);
                }catch(JSONException e){
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }
                final String[] p={"push.php","pseudo",uClicked.getPseudo(),"rid",rid};
                r = new Requete();
                r.execute(p);
                break;
            default:

        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        userSelected=(String)parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        userSelected=parent.getSelectedItem().toString();
    }
}

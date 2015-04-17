package com.manhattanproject.geolocalisation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class Activity_list_utilisateur extends Activity implements AdapterView.OnItemSelectedListener{
    private ExpandableListView expandableList;
    private AdapterListUtilisateur adaptor;
    private ArrayList<Utilisateur> listeUser=new ArrayList<>();
    private Utilisateur uClicked;
    private String userSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_utilisateur);
        expandableList = (ExpandableListView) findViewById(R.id.expandableListUtilisateur);
        Utilisateur courant = new Utilisateur();
        courant.recup(getApplicationContext());
        majListUtilisateur();
        registerForContextMenu(expandableList);
        adaptor = new AdapterListUtilisateur(this, listeUser);
        expandableList.setAdapter(adaptor);
        SharedPreferences settings = getSharedPreferences("DemandesNonLues", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor edit = settings.edit();
        edit.putInt("nb", 0);
        edit.apply();
    }

    private void majListUtilisateur(){
        Utilisateur courant = new Utilisateur();
        courant.recup(getApplicationContext());
        final String[] params={"selectUsersWaitingDemands.php","pseudo",courant.getPseudo()};
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
                listeUser.add(i,new Utilisateur(i, json_data.getString("pseudo"), null, json_data.getString("statut"), new LatLng(json_data.getDouble("positionx"),json_data.getDouble("positiony"))));
            }
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        //listeUser.remove(courant);
        //ArrayList<Ami> listeA = Activity_list_ami.recupereAmi(courant);
        //listeUser.removeAll(listeA);
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
            String [] menuItems=getResources().getStringArray(R.array.menuDemandesLongClick);
            for(int i=0; i<menuItems.length; i++){
                menu.add(Menu.NONE,i,i,menuItems[i]);
            } }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex=item.getItemId();
        String [] menuItems=getResources().getStringArray(R.array.menuDemandesLongClick);
        Utilisateur courant = new Utilisateur();
        courant.recup(getApplicationContext());
        final String[] params={"selectADemand.php","user",courant.getPseudo(),"ami",uClicked.getPseudo()};
        String iduser = null;
        String idami = null;
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
            idami=json_data.getString("fk_id_ut1");
            iduser=json_data.getString("fk_id_ut2");
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        switch(menuItemIndex){
            case 0: //accepter la demande

                final String[] p={"newFriend.php","iduser",iduser,"idami",idami};
                r = new Requete();
                r.execute(p);
                break;
            case 1 : //refuser la demande : la supprimer

                break;
            default:

        }
        final String[] p2={"deleteDemand.php","iduser",iduser,"idami",idami};
        r = new Requete();
        r.execute(p2);
        listeUser.clear();
        majListUtilisateur();
        adaptor.notifyDataSetChanged();
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

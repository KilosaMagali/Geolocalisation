package com.manhattanproject.geolocalisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kilosakeyrocker on 22/02/15.
 */
public class DataProvider {
    public static HashMap<String, List<String>> getInfo(ArrayList<String> ll1, ArrayList<String> ll2){
        HashMap<String, List<String>> lieuContainer = new HashMap<>();
     for(int i=0; i<ll1.size(); i++) {
         List<String> lieu1 = new ArrayList<>();
         lieu1.add("Location: "+ll1.get(i));
         lieu1.add("Categorie: "+ll2.get(i));
         lieuContainer.put(ll1.get(i), lieu1);


     }
        return lieuContainer;

    }

}

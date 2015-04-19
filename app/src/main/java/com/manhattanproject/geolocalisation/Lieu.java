package com.manhattanproject.geolocalisation;

/**
 * Created by vince_000 on 25/02/2015.
 */

import com.google.android.gms.maps.model.LatLng;

public class Lieu {
    long id;
    LatLng position;
    String designation;
    String description;
    Categorie_lieu categorie;
    boolean propose;

    public boolean isPropose() {
        return propose;
    }

    public void setPropose(boolean propose) {
        this.propose = propose;
    }

    boolean partage;

    public Lieu(Categorie_lieu categorie, String designation, String description, boolean partage, LatLng position) {
        this.categorie = categorie;
        this.description = description;
        this.designation = designation;
        this.partage = partage;
        this.position = position;
        this.id = -1;
    }

    public Lieu(long id,Categorie_lieu categorie, String designation, String description, boolean partage, LatLng position) {
        this.id = id;
        this.categorie = categorie;
        this.description = description;
        this.designation = designation;
        this.partage = partage;
        this.position = position;
    }

    public Lieu(long id,Categorie_lieu categorie, String designation, String description, boolean partage, LatLng position,boolean propose) {
        this.id = id;
        this.categorie = categorie;
        this.description = description;
        this.designation = designation;
        this.partage = partage;
        this.position = position;
        this.propose = propose;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Categorie_lieu getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie_lieu categorie) {
        this.categorie = categorie;
    }

    public boolean isPartage() {
        return partage;
    }

    public void setPartage(boolean partage) {
        this.partage = partage;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return this.id;
    }

    public String toString(){
        String s = new String("Lieu "+this.id+" : "+this.categorie+"\n"+this.designation+"\ndescription : \n"+this.description+"\n"+"position : "+this.position+"\n");
        if (this.partage)
            s = s+"lieu public";
        else
            s = s+"lieu prive";
        return s;
    }
}

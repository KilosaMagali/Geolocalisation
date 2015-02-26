package com.manhattanproject.geolocalisation;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vince_000 on 25/02/2015.
 */
public class Ami {
    long id;
    LatLng position;
    String nom;
    String prenom;
    String statut;
    boolean connect;

    public Ami(LatLng position, String nom, String prenom, String statut) {
        this.position = position;
        this.nom = nom;
        this.prenom = prenom;
        this.statut = statut;
        this.connect = false;
        this.id = -1;
    }

    public Ami(long id,LatLng position, String nom, String prenom, String statut) {
        this.id = id;
        this.position = position;
        this.nom = nom;
        this.prenom = prenom;
        this.statut = statut;
        this.connect = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public String toString(){
        String s = new String("Ami "+this.id+" :\n"+this.prenom+" "+this.nom+"\n"+"statut : "+this.statut+"\n");
        if (this.connect)
            s = s+"position : "+this.position;
        return s;
    }
}

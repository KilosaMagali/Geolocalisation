package com.manhattanproject.geolocalisation;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vince_000 on 25/02/2015.
 */
public class Ami implements java.lang.Comparable{
    long id;
    LatLng position;
    String pseudo;
    String statut;
    Bitmap image;
    boolean connect;

    int pos;
    boolean checked = false;

    public int getPos() {
        return pos;
    }

    public int compareTo(Object other) {
        return this.pseudo.compareTo (((Ami)other).getPseudo ());
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean getConnert(){return this.connect;}

    public Ami(LatLng position, String pseudo, String statut) {
        this.position = position;
        this.pseudo = pseudo;
        this.statut = statut;
        this.connect = false;
        this.id = -1;
    }

    public Ami(long id,LatLng position, String pseudo, String statut) {
        this.id = id;
        this.position = position;
        this.pseudo = pseudo;
        this.statut = statut;
        this.connect = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setImage(Bitmap im) {
        image = im;
    }

    public Bitmap getImage(){
        return this.image;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
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
        String s = new String("Ami "+this.id+" :\n"+this.pseudo+"\n"+"statut : "+this.statut+"\n");
        if (this.connect)
            s = s+"position : "+this.position;
        return s;
    }


}

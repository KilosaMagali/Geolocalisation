package com.manhattanproject.geolocalisation;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by lulu on 19/04/2015.
 */
public class uploadImg extends AsyncTask<Bitmap,Void,Boolean> {

    String nomUser;

    public uploadImg(String nomUser){
        this.nomUser=nomUser;
    }
    @Override
    protected Boolean doInBackground(Bitmap... params) {
        Bitmap bitmap = params[0];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
        byte [] byte_arr = stream.toByteArray();
        String image_str = com.manhattanproject.geolocalisation.Base64.encodeBytes(byte_arr);


        final ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("image",image_str));
        nameValuePairs.add(new BasicNameValuePair("nomUser",nomUser));

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://lmahot.hd.free.fr/img/uploadImg.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    System.out.println(response);
                }catch(Exception e){
                    System.out.println("Error in http connection "+e.toString());
                }
            }
        });
        t.start();
    return true;
    }
}

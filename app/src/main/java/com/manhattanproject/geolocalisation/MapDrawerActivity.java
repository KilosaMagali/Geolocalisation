package com.manhattanproject.geolocalisation;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapDrawerActivity extends FragmentActivity {

	private GoogleMap map;
    private RadioButton rbDriving;
    private RadioButton rbBiCycling;
    private RadioButton rbWalking;
    private RadioGroup rgModes;
    private ArrayList<LatLng> markerPoints;
    private int mMode=0;
	final int MODE_DRIVING=0;
	final int MODE_BICYCLING=1;
	final int MODE_WALKING=2;
    private Lieu lieuDestination;
    private LatLng  currentPosition;
    private Intent intent;
    private CameraPosition cameraPosition;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_drawer);
        intent=getIntent();
		// Getting reference to rb_driving
		rbDriving = (RadioButton) findViewById(R.id.rb_driving);
		
		// Getting reference to rb_bicylcing
		rbBiCycling = (RadioButton) findViewById(R.id.rb_bicycling);
		
		// Getting reference to rb_walking
		rbWalking = (RadioButton) findViewById(R.id.rb_walking);
		
		// Getting Reference to rg_modes
		rgModes = (RadioGroup) findViewById(R.id.rg_modes);
		
		rgModes.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
		
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {				
				
				// Checks, whether start and end locations are captured
				if(markerPoints.size() >= 2){					
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);
					
					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);				
					
					DownloadTask downloadTask = new DownloadTask();
					
					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
				}			
			}
		});
		
		// Initializing 
		markerPoints = new ArrayList<LatLng>();
		
		// Getting reference to SupportMapFragment of the activity_main
		SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
		
		// Getting Map for the SupportMapFragment
		map = fm.getMap();
		
		// Enable MyLocation Button in the Map
		map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        Location loc=map.getMyLocation();
        if(loc!=null)
        currentPosition=new LatLng(loc.getLatitude(),loc.getLongitude());
        //retrieve information passed from Activity_list_lieu
		retrieveLieuPassed();
        markerPoints.add(lieuDestination.getPosition());
        drawStartStopMarkers();
        if(currentPosition!=null)
            markerPoints.add(currentPosition);
		// Setting onclick event listener for the map
		map.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng point) {
				
				// Already two locations				
				if(markerPoints.size()>1){
					markerPoints.remove(1);
					map.clear();
                    drawStartStopMarkers();
				}
				
				// Adding new item to the ArrayList
				markerPoints.add(point);						
				
				// Draws Start and Stop markers on the Google Map
				drawStartStopMarkers();								
				
				// Checks, whether start and end locations are captured
				if(markerPoints.size() == 2){
					LatLng dest = markerPoints.get(0);
					LatLng origin = markerPoints.get(1);

					
					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);				
					
					DownloadTask downloadTask = new DownloadTask();
					
					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
				}
				
			}
		});

	}

    private void retrieveLieuPassed() {
       String nameLieu="FAILED_TO_PASS_NAME";
       if(intent.hasExtra("nameLieu"))
           nameLieu=getIntent().getStringExtra("nameLieu");
        String descriptionLieu="FAILED_TO_PASS_DESCRIPT";
        if(intent.hasExtra("descriptionLieu"))
            descriptionLieu=getIntent().getStringExtra("descriptionLieu");
       String categorieLieu="FAILED_TO_PASS_CATEG";
        if(intent.hasExtra("categoryLieu"))
              categorieLieu =getIntent().getStringExtra("categoryLieu");
        double latitudeLieu=getIntent().getDoubleExtra("latitudeLieu",0);
        double longitudeLieu=getIntent().getDoubleExtra("longitudeLieu", 0);
        boolean partage=getIntent().getBooleanExtra("partage",false);
        lieuDestination=new Lieu(Categorie_lieu.valueOf(categorieLieu),nameLieu,descriptionLieu,partage,new LatLng(latitudeLieu,longitudeLieu));


    }

    //Zoom to a location passed as a parameter
    public void zoomToAPoint(LatLng pos){
        cameraPosition = new CameraPosition.Builder().target(pos)
                .zoom(17).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    // Drawing Start and Stop locations
	private void drawStartStopMarkers(){
		int lastlyAdded=0;
		for(int i=0;i<markerPoints.size();i++){
		
			// Creating MarkerOptions
			MarkerOptions options = new MarkerOptions();
			
			
			// Setting the position of the marker
			options.position(markerPoints.get(i) );
			
			/** 
			 * For the start location, the color of marker is GREEN and
			 * for the end location, the color of marker is RED.
			 */
			if(i==0){
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			}else if(i==1){
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			}			
			lastlyAdded=i;
			// Add new marker to the Google Map Android API V2
			map.addMarker(options);
		}
        zoomToAPoint(markerPoints.get(lastlyAdded));
	}
	
	
	private String getDirectionsUrl(LatLng origin,LatLng dest){
					
		// Origin of route
		String str_origin = "origin="+origin.latitude+","+origin.longitude;
		
		// Destination of route
		String str_dest = "destination="+dest.latitude+","+dest.longitude;	
					
		// Sensor enabled
		String sensor = "sensor=false";			
		
		// Travelling Mode
		String mode = "mode=driving";	
		
		if(rbDriving.isChecked()){
			mode = "mode=driving";
			mMode = 0 ;
		}else if(rbBiCycling.isChecked()){
			mode = "mode=bicycling";
			mMode = 1;
		}else if(rbWalking.isChecked()){
			mode = "mode=walking";
			mMode = 2;
		}
		
					
		// Building the parameters to the web service
		String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+mode;
					
		// Output format
		String output = "json";
		
		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
		
		
		return url;
	}
	
	/** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url 
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url 
                urlConnection.connect();

                // Reading data from url 
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }
                
                data = sb.toString();

                br.close();

        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
     }

	
	
	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>{			
				
		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {
				
			// For storing data from web service
			String data = "";
					
			try{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;		
		}
		
		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);			
			
			ParserTask parserTask = new ParserTask();
			
			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
				
		}		
	}
	
	/** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
    	
    	// Parsing the data in non-ui thread    	
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
			
			JSONObject jObject;	
			List<List<HashMap<String, String>>> routes = null;			           
            
            try{
            	jObject = new JSONObject(jsonData[0]);
            	DirectionsJSONParser parser = new DirectionsJSONParser();
            	
            	// Starts parsing data
            	routes = parser.parse(jObject);    
            }catch(Exception e){
            	e.printStackTrace();
            }
            return routes;
		}
		
		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();
			
			// Traversing through all the routes
			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
				
				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);
				
				// Fetching all the points in i-th route
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);					
					
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);
					
					points.add(position);						
				}
				
				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(8);
				
				// Changing the color polyline according to the mode
				if(mMode==MODE_DRIVING)
					lineOptions.color(Color.RED);
				else if(mMode==MODE_BICYCLING)
					lineOptions.color(Color.GREEN);
				else if(mMode==MODE_WALKING)
					lineOptions.color(Color.BLUE);				
			}
			
			if(result.size()<1){
				Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
				return;
			}
			
			// Drawing polyline in the Google Map for the i-th route
			map.addPolyline(lineOptions);
			
		}			
    }   
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

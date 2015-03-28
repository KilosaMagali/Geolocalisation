package com.manhattanproject.geolocalisation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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

	private GoogleMap googleMap;
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
    private Location currentLocation;
    private static boolean gpsRequest=false;
    private Button btnShowPopUp,btnOkOptions,btnCancelOptions;
    private RadioButton rbFromCurrentPos;
    private RadioButton rbFromSpecifiedPos;
    private RadioGroup dir_modes;
    private PopupWindow popupOptions;
    private boolean  boolFromSpecifiedPos, boolFromCurrentPos;
    private TextView tvDistanceDuration;
    private DownloadTask downloadTask;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_drawer);
        intent=getIntent();
		// Getting reference to rb_driving
		rbDriving = (RadioButton) findViewById(R.id.rb_driving);
        // Restoring the markers on configuration changes
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey("gpsRequest")){
                gpsRequest=savedInstanceState.getBoolean("gpsRequest");
            }

        }
		// Getting reference to rb_bicylcing
		rbBiCycling = (RadioButton) findViewById(R.id.rb_bicycling);
		
		// Getting reference to rb_walking
		rbWalking = (RadioButton) findViewById(R.id.rb_walking);
		
		// Getting Reference to rg_modes
		rgModes = (RadioGroup) findViewById(R.id.rg_modes);

        //Getting Reference to TextField displaying distance and time
        tvDistanceDuration=(TextView)findViewById(R.id.tv_distance_time);
		
		rgModes.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
		
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

		        downloadAndDrawPath();
			}
		});
		
		// Initializing 
		markerPoints = new ArrayList<LatLng>();

        createMapView();
        //Retrieve all the infos passed from the previous activity
		retrieveLieuPassed();
        //add destination in the list
        markerPoints.add(lieuDestination.getPosition());
        //If no network ->alert
        if(!isNetworkAvailable()) {
            new AlertDialog.Builder(this)
                    .setTitle("Attention!")
                    .setMessage("Pas de Connexion Internet")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            currentPosition = getMyCurrentLocation();
            //drawStartStopMarkers();
            if (currentPosition != null) {
                markerPoints.add(currentPosition);
                googleMap.clear();
                drawStartStopMarkers();

                downloadAndDrawPath();
            }

            //Options button
            btnShowPopUp = (Button) findViewById(R.id.btnOptions);
            setUpPopup();
            boolFromCurrentPos = true;
            boolFromSpecifiedPos = false;
            //setMapOnClickListener();
        }
	}

    /**
     * Set up popup window for each click on options
     */
    private void setUpPopup() {
        btnShowPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflating popup layout
                View popUpView = getLayoutInflater().inflate(R.layout.popup_options, null);
                popupOptions = new PopupWindow(popUpView, LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT, true); //Creation of popup
                popupOptions.setAnimationStyle(android.R.style.Animation_Dialog);
                popupOptions.showAtLocation(popUpView, Gravity.TOP, 0, 30);    // Displaying popup
                btnCancelOptions=(Button)popUpView.findViewById(R.id.btnCancelOptions);
                btnOkOptions = (Button)popUpView.findViewById(R.id.btnOKOptions);
                dir_modes =(RadioGroup)popUpView.findViewById(R.id.dir_modes);
                rbFromCurrentPos=(RadioButton)popUpView.findViewById(R.id.rb_fromMyCurrentPos);
                rbFromSpecifiedPos=(RadioButton)popUpView.findViewById(R.id.rb_frmSpecifiedPos);
                rbFromCurrentPos.setChecked(boolFromCurrentPos);
                rbFromSpecifiedPos.setChecked(boolFromSpecifiedPos);
                dir_modes.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                    }
                });
                Button btnOk = (Button) popUpView.findViewById(R.id.btnOKOptions);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolFromCurrentPos=rbFromCurrentPos.isChecked();
                        boolFromSpecifiedPos=rbFromSpecifiedPos.isChecked();
                        setMapOnClickListener();
                        popupOptions.dismiss();  //dismissing the popup
                    }
                });

                Button btnCancel = (Button) popUpView.findViewById(R.id.btnCancelOptions);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupOptions.dismiss(); //dismissing the popup
                    }
                });
            }
        });
    }




    /**
     *  Setting onclick event listener for the map
     */
    public void setMapOnClickListener() {
        if (boolFromCurrentPos) googleMap.setOnMapClickListener(null);
        if(boolFromSpecifiedPos) {
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {

                    // Already two locations
                    if (markerPoints.size() > 1) {
                        markerPoints.remove(1);
                        googleMap.clear();
                        drawStartStopMarkers();
                    }

                    // Adding new item to the ArrayList
                    markerPoints.add(point);

                    // Draws Start and Stop markers on the Google Map
                    drawStartStopMarkers();

                    downloadAndDrawPath();

                }
            });
   }
    }

    /**
     *
     */
    private void createMapView(){
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the activity_map
         */

		// Getting reference to SupportMapFragment of the activity
		SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);


        try {
            if(null == googleMap){
                // Getting Map for the SupportMapFragment
                googleMap = fm.getMap();

                // Enable MyLocation Button in the Map
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                /**
                 * If the activity_map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if(null == googleMap) {
                    Toast.makeText(getApplicationContext(),
                            "Error creating map", Toast.LENGTH_SHORT).show();
                }
                googleMap.clear();
            }
        } catch (NullPointerException exception){
            Log.e("Geolocalisation", exception.toString());
        }
    }
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS est desactivé. Veuillez l'activer pour plus de précision?")
                .setCancelable(false)
                .setPositiveButton("Activer GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Continuer sans GPS",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    /**
     * Download json data and draw the path on the map.
     */
    private void downloadAndDrawPath() {
        if(isNetworkAvailable()) {
        googleMap.clear();

            // Checks, whether start and end locations are captured
            if (markerPoints.size() == 2) {
                LatLng dest = markerPoints.get(0);
                LatLng origin = markerPoints.get(1);
                drawStartStopMarkers();

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        } else{

                new AlertDialog.Builder(this)
                        .setTitle("Attention!")
                        .setMessage("Pas de Connexion Internet")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


        }
    }

    private LatLng getMyCurrentLocation() {
        //Enable MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);

        LocationProvider locationProvider = new LocationProvider();

        //Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        currentPosition=locationProvider.getLocation();
        Location loc= googleMap.getMyLocation();
        if(loc!=null) return new LatLng(loc.getLatitude(),loc.getLongitude());
        else
            return currentPosition;
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
                .zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    // Drawing Start and Stop locations
	private void drawStartStopMarkers(){
        googleMap.clear();
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
			googleMap.addMarker(options);
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
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;


        try {
            URL url = new URL(strUrl);
                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
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
            String distance = "";
            String duration = "";
			
			// Traversing through all the routes
			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
				
				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);
				
				// Fetching all the points in i-th route
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }
					
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
				Toast.makeText(getBaseContext(), "Pas de Schemin ", Toast.LENGTH_SHORT).show();
				return;
			}
            //Display distance and duration to destination
            tvDistanceDuration.setText("Distance: "+distance + ", Durée: "+duration);
			// Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
			
		}			
    }   
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    /**
     *A Class to Manage current location using GPS.
     */
    private class LocationProvider implements LocationListener {
        private double latitude, longitude;
        private String providerName;
        private final int TIME_INTERVAL=15000;
        private final String NETWORK_PROV=LocationManager.NETWORK_PROVIDER;
        private final String GPS_PROV=LocationManager.GPS_PROVIDER;

        public LocationProvider() {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            if (!lm.isProviderEnabled(GPS_PROV) && !gpsRequest){
                showGPSDisabledAlertToUser();
            }

            if (lm.isProviderEnabled(GPS_PROV)){
                providerName=GPS_PROV;
            }
            else
                providerName=NETWORK_PROV;


            Location lastLocation = lm.getLastKnownLocation(providerName);
            if (lastLocation != null) {
                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();
            }
            lm.requestLocationUpdates(providerName, TIME_INTERVAL, 0, this);

        }

        public LatLng getLocation(){
            currentLocation= googleMap.getMyLocation();
            if(currentLocation!=null)
                currentPosition=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
            else
                   currentPosition= new LatLng(latitude,longitude);

            return currentPosition;
        }

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                googleMap.clear();
                if(!boolFromSpecifiedPos) {
                    markerPoints.remove(1);
                    markerPoints.add(getLocation());
                }
                drawStartStopMarkers();
                downloadAndDrawPath();
            }


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            googleMap.clear();
            drawStartStopMarkers();
            downloadAndDrawPath();
        }

        @Override
        public void onProviderEnabled(String provider) {
            //if GPS has been activated, use it a provider
            if(provider.equals(GPS_PROV)) providerName = GPS_PROV;
            LocationManager lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location=lm.getLastKnownLocation(providerName);
            if(location!=null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                googleMap.clear();
                if(!boolFromSpecifiedPos) {
                    markerPoints.remove(1);
                    markerPoints.add(getLocation());
                }
                drawStartStopMarkers();
                downloadAndDrawPath();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        if(provider.equals(GPS_PROV))
            providerName = NETWORK_PROV;
            googleMap.clear();
            if(!boolFromSpecifiedPos) {
                markerPoints.remove(1);
                markerPoints.add(getMyCurrentLocation());
            }
            drawStartStopMarkers();
            downloadAndDrawPath();
        }
    }

    /**
     * Checking internet connexion
     * @param
     * @return boolean:true if has intenet connection
     * boolean:false otherwise
     */
    public   boolean hasInternetAccess() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://www.google.com")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500*30);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("TAG_INTERNET_ERR", "Error checking internet connection", e);
            }
        } else {
            Log.d("TAG_INTERNET_ERR", "No network available!");
        }
        return false;
    }

    /**
     *
     * @return boolean: true if wifi or mobile data is activated
     * and boolean:false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    // A callback method, which is invoked on configuration is changed
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Adding the gsp and network variables

        outState.putBoolean("gpsRequest",gpsRequest);
        outState.putBoolean("boolFromCurrentPos",boolFromCurrentPos);
        outState.putBoolean("boolFromSpecifiedPos",boolFromSpecifiedPos);

        // Saving the bundle
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
       if(downloadTask!=null){
           if(!downloadTask.isCancelled())
                 downloadTask.cancel(true);
       }

    }


}

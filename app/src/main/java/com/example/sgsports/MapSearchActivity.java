package com.example.sgsports;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
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

public class MapSearchActivity extends BaseActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private GoogleMap map;

    LinearLayout infoL;
    ListView reviewList;

    TextView namefac;
    TextView typefac;
    TextView address;

    LinearLayout reviewL;
    ScrollView dirScroll;

    ArrayList<ReviewData> reviews;

    Facility curFac;
    Facility facility_clicked;

    //new
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;
    Location mLastLocation;

    //Directions variables
    private LatLng curPosition;
    private LatLng curDest;
    private Polyline mPolyline;
    private boolean isOnDirectionRoute = false;
    private ArrayList<String> directions;
    ArrayList<LatLng> mMarkerPoints;
    TextView directionInstr;

    CollectionReference facilityref;
    //firebase database
    FirebaseFirestore mFireStore;
    ArrayList<Facility> allFacilities;

    private ChildEventListener mChildEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_map, contentFrameLayout);
        mMarkerPoints = new ArrayList<>();


        namefac = findViewById(R.id.name);
        typefac = findViewById(R.id.typeText);
        address = findViewById(R.id.addrText);
        directions      = new ArrayList<>();

        //initialize map and current location
        initMap();
        //book button
        findViewById(R.id.bookapp).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                if(curFac!=null) {
                    Intent intent = new Intent(MapSearchActivity.this, BookAppointmentActivity.class);
                    intent.putExtra("facility", curFac);
                    startActivity(intent);
                }
            }
        });
        //write a review button
        findViewById(R.id.writereview).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                if(curFac!=null) {
                    Intent intent = new Intent(MapSearchActivity.this, WriteReviewActivity.class);
                    intent.putExtra("facility", curFac);
                    startActivity(intent);
                }
            }
        });

        infoL = (LinearLayout)findViewById(R.id.infoLayout);
        reviewList = (ListView)findViewById(R.id.reviewList);
        directionInstr = findViewById(R.id.locinfo);

        reviewL = (LinearLayout)findViewById(R.id.reviewlayout);
        dirScroll = (ScrollView)findViewById(R.id.dirScroll);

        //information button
        findViewById(R.id.infoB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                infoL.setVisibility(View.VISIBLE);
                reviewL.setVisibility(View.GONE);
                dirScroll.setVisibility(View.GONE);
            }
        });

        //review list button
        findViewById(R.id.reviewB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                infoL.setVisibility(View.GONE);
                reviewL.setVisibility(View.VISIBLE);
                dirScroll.setVisibility(View.GONE);
            }
        });

        //directions button
        findViewById(R.id.directionsB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                infoL.setVisibility(View.GONE);
                reviewL.setVisibility(View.GONE);
                dirScroll.setVisibility(View.VISIBLE);
            }
        });
    }


    private void readData(final FireStoreCallback fireStoreCallback){
        facilityref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document:task.getResult()){
                        Facility facility = document.toObject(Facility.class);
                        allFacilities.add(facility);
                    }
                    fireStoreCallback.onCallback(allFacilities);
                }
            }
        });
    }
    private interface FireStoreCallback{
        void onCallback(ArrayList<Facility> List);
    }
    void initMap(){
        //set Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        // Dont clutter map screen
        map.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mFireStore = FirebaseFirestore.getInstance();
        allFacilities = new ArrayList<>();
        facilityref = mFireStore.collection("Facility");

        readData(new FireStoreCallback() {
            private static final String TAG = "error";

            @Override
            public void onCallback(ArrayList<Facility> list) {
                for (int i = 0; i < allFacilities.size(); i++) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng location = new LatLng(allFacilities.get(i).getLatitude(), allFacilities.get(i).getLongitude());
                    markerOptions.position(location);
                    String name = allFacilities.get(i).getName();
                    markerOptions.title(name);
                    markerOptions.snippet(allFacilities.get(i).getType());
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    map.addMarker(markerOptions);
                }
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // curPosition should be initialized before we arrive here.
                isOnDirectionRoute = true;
                drawRoute(curPosition, marker.getPosition(), "walking");
                curDest = marker.getPosition();


                String markertitle = marker.getTitle();
                for (Facility fac : allFacilities){
                    String checkname = fac.getName();
                    // check the marker with the database data
                    if (checkname.equals(markertitle)) {
                        namefac.setText(markertitle);
                        String type = fac.getType();
                        if(type.startsWith(" "))
                            type = type.substring(1);
                        typefac.setText(type.replaceAll(" ", ", "));
                        address.setText(fac.getAddress());
                        curFac = fac;
                        break;
                    }
                }

                getReview(markertitle);
                infoL.setVisibility(View.VISIBLE);
                reviewL.setVisibility(View.GONE);
                dirScroll.setVisibility(View.GONE);
//
//
                return false;
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                isOnDirectionRoute = false;
                if (directionInstr != null && mPolyline != null){
                    directionInstr.setVisibility(View.GONE);
                    mPolyline.remove();
                }
            }
        });



        mLocationRequest = new LocationRequest();
        //  mLocationRequest.setInterval(120000); // two minute interval
        //mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                map.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            map.setMyLocationEnabled(true);
        }

        //search item shown
        Intent intent = getIntent();
        facility_clicked = (Facility) intent.getSerializableExtra("facility");
        if(facility_clicked!=null){
            namefac.setText(facility_clicked.getName());
            String type = facility_clicked.getType();
            if(type.startsWith(" "))
                type = type.substring(1);
            typefac.setText(type.replaceAll(" ", ", "));
            address.setText(facility_clicked.getAddress());
            LatLng latLng = new LatLng(facility_clicked.getLatitude(), facility_clicked.getLongitude());
            curFac = facility_clicked;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

            curDest = latLng;
            isOnDirectionRoute=true;

            getReview(facility_clicked.getName());
        }
    }

    private void getLastKnownLocation() {
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    getLastKnownLocation();
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                //Place current location marker
                curPosition = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(curPosition);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                mCurrLocationMarker = map.addMarker(markerOptions);

                //move map camera
               // map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPosition, 13));

                /** Each time location updates we have to redraw the direction route */
                if (isOnDirectionRoute){
                    Log.d("location update", "location updated so redrawing route!");
                    if (mPolyline != null)
                        mPolyline.remove();
                    drawRoute(curPosition, curDest, "walking");
                }
            }
        }};

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapSearchActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        } }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        map.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    void getReview(String name){
        reviews = new ArrayList<>();
        mFireStore.collection("Review").whereEqualTo("facilityName", name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        ReviewData review = doc.toObject(ReviewData.class);
                        reviews.add(review);
                    }

                    ReviewListAdapter adapter = new ReviewListAdapter(reviews, getApplicationContext());
                    reviewList.setAdapter(adapter);

                    reviewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //get review data
                            ReviewData review = reviews.get(i);

                            //review detail page
                            Intent intent = new Intent(getApplicationContext(), ReadActivity.class);
                            intent.putExtra("review", review);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

    }

    /**
     * Method to draw a route between two points using googles
     * direction api.
     *
     * First the request URL is built and sent.
     *
     * The JSON response is parsed, and converted into a list
     * of points which ar
     *
     */
    private void drawRoute(LatLng orig, LatLng dest, String moveType) {
        // Download, parse and display the google directions task.
        new DownloadTask().execute(getDirectionsUrl(orig, dest, moveType));
    }

    /**
     * A method to build a request URL to google direction api.
     *
     * Move types include: driving/walking/bicycling/transit
     * */
    private String getDirectionsUrl(LatLng orig, LatLng dest, String modeType) {
        if (!modeType.equals("driving") && !modeType.equals("walking") &&
                !modeType.equals("bicycling") && !modeType.equals("transit")){
            Log.d("Direction API URL", "Invalid moving type: '" + modeType + "'");
            modeType = "driving"; //default is driving
        }
        Log.d("Direction API URL", "Moving type: '" + modeType + "'");

        String urlOrig  = "origin="      + orig.latitude + "," + orig.longitude;
        String urlDest  = "destination=" + dest.latitude + "," + dest.longitude;
        String urlMode  = "mode="        + modeType;
        String urlKey   = "key="         + getString(R.string.DIRECTION_API_KEY);
        String parameters = urlOrig + "&" + urlDest + "&" + urlMode + "&" + urlKey;

        // Complete request URL
        return "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
    }

    /**
     * A method to download json data from url
     */
    private static String downloadUrl(String strUrl) throws IOException {
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
        } catch (Exception e) {
            Log.d("Exception on download", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to download JSON-data from Google Directions URL
     * in non-UI thread asynchronous, which is done to not slow
     * down and keep main UI thread running smooth.
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            String jsonData = "";
            try {
                // Fetching the data from web service
                jsonData = downloadUrl(url[0]);
                Log.d("DownloadTask", "DownloadTask : " + jsonData);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return jsonData;
        }

        // Executes in UI thread, after the execution of doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                routes = parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            if (result == null){
                Log.e("Directions draw", "I have nothing to draw!");
                return;
            }
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                directionInstr.setVisibility(View.VISIBLE);

                if (mPolyline != null) {
                    mPolyline.remove();
                }
                mPolyline = map.addPolyline(lineOptions);
            } else
                Toast.makeText(getApplicationContext(), "No route is found", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * JSON parser for directions
     */
    private List<List<HashMap<String, String>>> parse(JSONObject jObject) {
        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes   = null;
        JSONArray jLegs     = null;
        JSONArray jSteps    = null;
        directions.clear();

        try {
            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);
                        directions.add(((JSONObject)jSteps.get(k)).get("html_instructions").toString());
                        Log.d("DIRECTION API", ((JSONObject)jSteps.get(k)).get("html_instructions").toString());
                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString((list.get(l)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    if (directions.size() > 0){
                        StringBuilder sb = new StringBuilder();
                        for (String s : directions) {
                            sb.append(s).append("<p>");
                        }
                        directionInstr.setText(Html.fromHtml(sb.toString()));
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return routes;
    }

    /**
     * Method to decode polyline points. (Converting a string into LatLng points)
     * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * https://developers.google.com/maps/documentation/utilities/polylinealgorithm
     */
    private static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

}
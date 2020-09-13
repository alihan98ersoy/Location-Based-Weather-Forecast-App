package com.alihan98ersoy.locationbasedweather.viewmodels;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alihan98ersoy.locationbasedweather.R;
import com.alihan98ersoy.locationbasedweather.models.Card;
import com.alihan98ersoy.locationbasedweather.models.CardArrayAdapter;
import com.alihan98ersoy.locationbasedweather.models.theme.InitApplication;
import com.alihan98ersoy.locationbasedweather.services.CurrentWeather;
import com.alihan98ersoy.locationbasedweather.services.CurrentWeatherService;
import com.alihan98ersoy.locationbasedweather.services.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mahfa.dnswitch.DayNightSwitchAnimListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class homepage_activity extends AppCompatActivity implements OnMapReadyCallback {

 private InitApplication init;
 private Firebase firebase = new Firebase(FirebaseApp.getInstance(),this);
 private Button logoutbtn;
 private FirebaseAnalytics mFirebaseAnalytics;
 private Context context;


    private CurrentWeatherService currentWeatherService;


    private EditText locationField;
    TextView currentcitytextview;
    private Button fab;
    private int textCount = 0;
    private boolean fetchingWeather = false;
    private String currentLocation = "Istanbul";

    private SharedPreferences mPrefs;


    private RecyclerView recyclerView;
    private ArrayList<Card> cardlist = new ArrayList<Card>();
    private CardArrayAdapter adapter;

    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyAbN1_w3VJxI7QLbb3W6i1lP4KISpVjgXA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init = new InitApplication(this);
        if (init.isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_homepage_activity);
        final com.mahfa.dnswitch.DayNightSwitch daynight = findViewById(R.id.day_night_switch);
        logoutbtn = findViewById(R.id.logout);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Weather part
        currentWeatherService = new CurrentWeatherService(this);
        locationField = findViewById(R.id.location_field);
        fab = findViewById(R.id.fab);
        fab.setBackground(getResources().getDrawable(R.drawable.ic_refresh));

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        currentcitytextview = findViewById(R.id.currentcitytextview);


        mPrefs = this.getSharedPreferences("sharedpref",Context.MODE_PRIVATE);
        currentLocation = mPrefs.getString("City", "Istanbul");
        currentcitytextview.setText(currentLocation);


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        //mapView.getMapAsync((OnMapReadyCallback) this); //its doing initialize map inside searchForWeather method too;


        searchForWeather(currentLocation);
        context=this;

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));


        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            daynight.setIsNight(true);


        daynight.setAnimListener(new DayNightSwitchAnimListener() {
            @Override
            public void onAnimStart() {

            }

            @Override
            public void onAnimEnd() {
                if(daynight.isNight())
                {
                    init.setIsNightModeEnabled(true);
                }else
                {
                    init.setIsNightModeEnabled(false);
                }
            }

            @Override
            public void onAnimValueChanged(float value) {
                if (value==1) {


                } else {

                }
                onRestart();
            }
        });

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[][] array = {{"Uid",firebase.getCurrentUser().getUid()},{"Email",firebase.getCurrentUser().getEmail()}};
                firebase.UseAnalytic("Logout",array,mFirebaseAnalytics);
                firebase.signOut();
                startActivity(new Intent(homepage_activity.this, login_activity.class));
                finish();
            }
        });


        locationField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                count = s.toString().trim().length();
                fab.setBackground(getResources().getDrawable(count == 0 ? R.drawable.ic_refresh : R.drawable.ic_search));
                textCount = count;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textCount == 0) {
                    refreshWeather();
                } else {
                    currentLocation = locationField.getText().toString();

                    String[][] array = {{"currentLocation",currentLocation}};
                    firebase.UseAnalytic("ChangecurrentLocation",array,mFirebaseAnalytics);

                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("City", currentLocation); // change current location for restart app
                    editor.apply();


                    currentcitytextview.setText(currentLocation);
                    cardlist.clear();
                    searchForWeather(currentLocation);
                    locationField.setText("");
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }


    private void refreshWeather() {
        if (fetchingWeather) {
            return;
        }
        searchForWeather(currentLocation);
    }

     private void searchForWeather(@NonNull final String location) {
         toggleProgress(true);


            fetchingWeather = true;
            currentWeatherService.getCurrentWeather(location, currentWeatherCallback);

            mapView.getMapAsync((OnMapReadyCallback) this);
    }

    private void toggleProgress(final boolean showProgress) {
       /* weatherContainer.setVisibility(showProgress ? View.GONE : View.VISIBLE);
        weatherProgressBar.setVisibility(showProgress ? View.VISIBLE : View.GONE);*/
    }

    private final CurrentWeatherService.CurrentWeatherCallback currentWeatherCallback = new CurrentWeatherService.CurrentWeatherCallback() {

        @Override
        public void onCurrentWeather(@NonNull CurrentWeather currentWeather) {

            String[] date = currentWeather.getLocation().split(" ");//location is date also  first element day second hour
            String[] yearmonthday = date[0].split("-");//year-month-day
            String[] hourmin=date[1].split(":");//hour:minute

            String[] timestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime()).split("_");//yyyy_MM_dd_HH_mm_ss


            if(hourmin[0].equals("06")||hourmin[0].equals("12")||hourmin[0].equals("18")){//data has every 3 hours data so I needed to do 6 hour for design
            Card card = new Card(currentWeather.getConditionId(),currentWeather.getWeatherCondition(),currentWeather.getCelcius(),yearmonthday[1]+"-"+yearmonthday[2]+"\n"+hourmin[0]+":00");
            cardlist.add(card);
            System.out.println("!! homepage_activity CurrentWeatherService.CurrentWeatherCallback curent weather: "+currentWeather);

            adapter = new CardArrayAdapter(context, cardlist);
            recyclerView.setAdapter(adapter);
            toggleProgress(false);
            fetchingWeather = false;}
        }

        @Override
        public void onError(@Nullable Exception exception) {
            toggleProgress(false);
            fetchingWeather = false;
            Toast.makeText(homepage_activity.this, "There was an error fetching weather, " +
                    "try again.", Toast.LENGTH_SHORT).show();
        }
    };




     //map
     @Override
     public void onSaveInstanceState(Bundle outState) {
         super.onSaveInstanceState(outState);

         Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
         if (mapViewBundle == null) {
             mapViewBundle = new Bundle();
             outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
         }

         mapView.onSaveInstanceState(mapViewBundle);
     }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        currentWeatherService.cancel();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        String location = currentLocation;
        Geocoder gc = new Geocoder(this);
        List<Address> addresses;
        List<LatLng> ll;
        LatLng ny = new LatLng(40.7143528, -74.0059731);
        gmap = googleMap;
        gmap.setMinZoomPreference(10);
        if(init.isNightModeEnabled()) {//night mode for map
            gmap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
        }
        if(Geocoder.isPresent()){
            try {
                addresses= gc.getFromLocationName(location, 5); // get the found Address Objects
                ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    }
                }
                String latlng = ll.toString().split(" ")[1];
                String lat = latlng.split(",")[0].replaceFirst("\\(","");
                String lang = latlng.split(",")[1].replaceFirst("\\)","");
                lang = lang.replace("]","");
                System.out.println("!! homepage onMapReady ll: "+lat+"  "+lang);
                gmap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat),Double.parseDouble(lang)) ));
            } catch (IOException e) {
                // handle the exception
                System.out.println("!! homepage onMapReady IOException: "+e.toString());
                gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
            }
        }else
            {
                gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
        }


    }



}
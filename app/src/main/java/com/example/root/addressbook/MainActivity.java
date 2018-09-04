package com.example.root.addressbook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.root.addressbook.place.EditPlace;
import com.example.root.addressbook.place.PlaceDetails;
import com.example.root.addressbook.place.favorite.FavoritePlaces;
import com.example.root.addressbook.user.Login;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DatabaseReference placeReference;
    private FirebaseAuth mAuth;
    private String uid;
    private FirebaseUser user;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private double lastlat;
    private double lastlong;
    private ImageView mPickePlace;
    private ImageView favoritePlaces;
    private int PLACE_PICKER_REQUEST = 100;
    private List<PlaceDetails> list;
    private List<String> places_keys;
    private ImageView btnLogOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();
        places_keys = new ArrayList<>();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mPickePlace = (ImageView) findViewById(R.id.place_picker);
        favoritePlaces = (ImageView) findViewById(R.id.favorite_places);
        btnLogOut = (ImageView) findViewById(R.id.log_out);

        // init firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        placeReference = FirebaseDatabase.getInstance().getReference().child("users");

        CheckLogin(user);


        // Select place button
        mPickePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });


        // favorite place button
        favoritePlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this , FavoritePlaces.class));

            }
        });


        // logout user
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();
                startActivity(new Intent(MainActivity.this , Login.class));
                finish();

            }
        });



    }


    private void CheckLogin(FirebaseUser firebaseUser) {

        // Checking Login or not
        if (firebaseUser == null) {

            startActivity(new Intent(MainActivity.this, Login.class));
            finish();

        } else {

            uid = mAuth.getCurrentUser().getUid().toString();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        setMyPlacesIcons();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // permission for location opening
        getLocationPermission();

        getLastLocation();

        // Add a marker in Sydney and move the camera
        LatLng lastPlace = new LatLng(lastlat, lastlong);
        mMap.addMarker(new MarkerOptions().position(lastPlace)
                .title("Last Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_loc)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPlace, 15));


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                LatLng l = marker.getPosition();
                getLocationDetails(l);

                return false;
            }
        });


    }

    private void getLocationDetails(LatLng latLng) {

        for (int i = 0; i < list.size(); i++){

            PlaceDetails p = list.get(i);
            LatLng l = new LatLng(Double.valueOf(p.getPlace_latti()) , Double.valueOf(p.getPlace_langi()));

            if (l.latitude == latLng.latitude && l.longitude == latLng.longitude){

                place_data.placeDetails = p;
                place_data.placeKey = places_keys.get(i);
                startActivity(new Intent(MainActivity.this, EditPlace.class));

                break;
            }

        }

    }



    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null){

            lastlat = location.getLatitude();
            lastlong = location.getLongitude();;

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK){

                Place place = PlacePicker.getPlace(this,data);

                // Start Save Place Activity
                Intent intent = new Intent(MainActivity.this , SaveLocation.class);
                place_data.place = place;
                startActivity(intent);


            }
        }

    }

    private void setMyPlacesIcons(){


        placeReference.child(uid).child("my_places").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    PlaceDetails placeDetails = snapshot.getValue(PlaceDetails.class);
                    String type = placeDetails.getPlace_type();
                    String latti = placeDetails.getPlace_latti();
                    String longi = placeDetails.getPlace_langi();

                    LatLng latLng = new LatLng(Double.valueOf(latti) , Double.valueOf(longi));
                    SetIcons(type , latLng);

                    list.add(placeDetails);
                    places_keys.add(snapshot.getKey().toString());

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void SetIcons(String type , LatLng latLng) {


        switch (type){

            case "Home":

                mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.home)));
                break;

            case  "Restaurant":

                mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant)));
                break;

            case "Park":

                mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.park)));
                break;

        }

    }



    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        123);

            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    123);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case 123:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            return;
                        }
                    }
                }
            }
        }
    }


}
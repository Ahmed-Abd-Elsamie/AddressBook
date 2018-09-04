package com.example.root.addressbook.place.favorite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.root.addressbook.R;
import com.example.root.addressbook.place.PlaceDetails;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritePlaces extends AppCompatActivity {

    private ListView listView;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private List<PlaceDetails> list;
    private PlacesAdapter adapter;
    private String uid;
    private List<String> places_keys;
    private ProgressBar favProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_places);

        list = new ArrayList<>();
        places_keys = new ArrayList<>();

        // Assign Views
        listView = (ListView) findViewById(R.id.list_places);
        favProgress = (ProgressBar) findViewById(R.id.prog_fav);


        // init firebase
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");


        // getting my saved places
        MyPlaces();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

    }



    private void MyPlaces(){

        favProgress.setVisibility(View.VISIBLE);
        databaseReference.child(uid).child("my_places").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    PlaceDetails placeDetails = snapshot.getValue(PlaceDetails.class);
                    list.add(placeDetails);
                    places_keys.add(snapshot.getKey().toString());

                }

                adapter = new PlacesAdapter(FavoritePlaces.this , list , places_keys) ;
                listView.setAdapter(adapter);

                favProgress.setVisibility(View.INVISIBLE);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
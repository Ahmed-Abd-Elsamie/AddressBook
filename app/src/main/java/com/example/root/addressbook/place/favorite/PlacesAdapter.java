package com.example.root.addressbook.place.favorite;

/**
 * Created by root on 04/09/18.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.addressbook.R;
import com.example.root.addressbook.place.EditPlace;
import com.example.root.addressbook.place.PlaceDetails;
import com.example.root.addressbook.place_data;

import java.util.List;


/**
 * Created by Last Ahmed on 7/2/2018.
 */

public class PlacesAdapter extends ArrayAdapter<PlaceDetails> {

    private Activity context;
    private List<PlaceDetails> friends;
    private List<String> places_keys;

    public PlacesAdapter(Activity context,List<PlaceDetails>friends , List<String> places_keys) {
        super(context, R.layout.item_place,friends);
        this.context = context;
        this.friends = friends;
        this.places_keys = places_keys;
    }



    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.item_place,parent,false);
        TextView name = (TextView) view.findViewById(R.id.txt_place_name);
        ImageView placeImg = (ImageView) view.findViewById(R.id.img_place);
        Button btnEdit = (Button) view.findViewById(R.id.btn_edit);

        final PlaceDetails placeDetails = getItem(position);
        name.setText(placeDetails.getPlace_name());

        if (placeDetails.getPlace_type().equals("Home")){
            placeImg.setBackgroundResource(R.drawable.home);
        }else if (placeDetails.getPlace_type().equals("Restaurant")){
            placeImg.setBackgroundResource(R.drawable.restaurant);
        }else if (placeDetails.getPlace_type().equals("Park")){
            placeImg.setBackgroundResource(R.drawable.park);
        }


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                place_data.placeDetails = placeDetails;
                place_data.placeKey = places_keys.get(position);
                context.startActivity(new Intent(context , EditPlace.class));

            }
        });

        return view;
    }
}
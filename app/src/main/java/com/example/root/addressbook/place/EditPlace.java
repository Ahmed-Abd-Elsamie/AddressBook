package com.example.root.addressbook.place;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.addressbook.MainActivity;
import com.example.root.addressbook.R;
import com.example.root.addressbook.SaveLocation;
import com.example.root.addressbook.place_data;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditPlace extends AppCompatActivity {

    private EditText txtEditLocName;
    private EditText txtLocWeb;
    private EditText txtLocPhone;
    private EditText txtEditLat;
    private EditText txtEditLan;
    private EditText txtDesc;
    private Spinner placeType;
    private ProgressBar saveProgress;
    private Button btnSave;
    private DatabaseReference placeReference;
    private FirebaseAuth mAuth;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place);


        // Assign Views
        txtEditLocName = (EditText) findViewById(R.id.txt_location_edit_name);
        txtLocWeb = (EditText) findViewById(R.id.txt_location_web);
        txtLocPhone = (EditText) findViewById(R.id.txt_location_phone);
        txtDesc = (EditText) findViewById(R.id.txt_location_desc);
        txtEditLat = (EditText) findViewById(R.id.txt_lat);
        txtEditLan = (EditText) findViewById(R.id.txt_lan);
        placeType = (Spinner) findViewById(R.id.location_type);
        saveProgress = (ProgressBar) findViewById(R.id.prog_save);
        btnSave = (Button) findViewById(R.id.btn_save);


        // init firebase
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid().toString();
        placeReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        GetLocationDetails();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdateData();

            }
        });


    }

    private void UpdateData() {

        String placeName = txtEditLocName.getText().toString();
        String placePhoneNum = txtLocPhone.getText().toString();
        String webSite = txtLocWeb.getText().toString();
        String desc = txtDesc.getText().toString();
        String placeT = placeType.getSelectedItem().toString();
        String lat = txtEditLat.getText().toString();
        String lan = txtEditLan.getText().toString();


        if (TextUtils.isEmpty(placeName)){
            txtEditLocName.setError("Select name");
            return;
        }
        if (placeName.length() < 4){
            txtEditLocName.setError("not less than 4 chars");
            return;
        }
        if (TextUtils.isEmpty(placePhoneNum)){
            txtLocPhone.setError("enter a phone number");
            return;
        }
        if (placeType.getSelectedItem().toString().equals("select place type")){
            Toast.makeText(this, "Error , Please Select PLace Type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(webSite)){
            txtLocWeb.setText("");
        }
        if (TextUtils.isEmpty(desc)){
            txtDesc.setText("");
        }

        saveProgress.setVisibility(View.VISIBLE);

        Map<String , String> map = new HashMap<>();
        map.put("place_name" , placeName);
        map.put("place_phone" , placePhoneNum);
        map.put("place_web" , webSite);
        map.put("place_desc" , desc);
        map.put("place_type" , placeT);
        map.put("place_latti" , lat);
        map.put("place_langi" , lan);

        // Pushing to database
        placeReference.child("my_places").child(place_data.placeKey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    saveProgress.setVisibility(View.INVISIBLE);
                    // start main screen
                    startActivity(new Intent(EditPlace.this , MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right , R.anim.slide_out_right);
                    finish();

                }else {
                    saveProgress.setVisibility(View.INVISIBLE);
                }

            }
        });

    }


    private void GetLocationDetails() {


        txtEditLocName.setText(place_data.placeDetails.getPlace_name());
        txtLocPhone.setText(place_data.placeDetails.getPlace_phone());
        txtEditLat.setText(place_data.placeDetails.getPlace_latti());
        txtEditLan.setText(place_data.placeDetails.getPlace_langi());

        if (place_data.placeDetails.getPlace_type().equals("Home")){
            placeType.setSelection(1);
        }else if (place_data.placeDetails.getPlace_type().equals("Restaurant")){
            placeType.setSelection(2);
        }else if (place_data.placeDetails.getPlace_type().equals("Park")){
            placeType.setSelection(3);
        }

        if (place_data.placeDetails.getPlace_web() != null){
            txtLocWeb.setText(place_data.placeDetails.getPlace_web());
        }

        if (place_data.placeDetails.getPlace_desc() != null){
            txtDesc.setText(place_data.placeDetails.getPlace_desc());
        }


    }
}

package com.example.root.addressbook;

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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SaveLocation extends AppCompatActivity {

    private TextView txtLocName;
    private TextView txtLocPosition;
    private EditText txtEditLocName;
    private EditText txtLocWeb;
    private EditText txtLocPhone;
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
        setContentView(R.layout.activity_save_location);

        // Assign Views
        txtLocName = (TextView) findViewById(R.id.txt_location_name);
        txtLocPosition = (TextView) findViewById(R.id.txt_location_edit_name);
        txtEditLocName = (EditText) findViewById(R.id.txt_location_edit_name);
        txtLocWeb = (EditText) findViewById(R.id.txt_location_web);
        txtLocPhone = (EditText) findViewById(R.id.txt_location_phone);
        txtDesc = (EditText) findViewById(R.id.txt_location_desc);
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

                saveData();

            }
        });




    }

    private void saveData() {

        String placeName = txtEditLocName.getText().toString();
        String placePhoneNum = txtLocPhone.getText().toString();
        String webSite = txtLocWeb.getText().toString();
        String desc = txtDesc.getText().toString();
        String placeT = placeType.getSelectedItem().toString();
        LatLng latLng = place_data.place.getLatLng();

        if (TextUtils.isEmpty(placeName)){
            txtEditLocName.setError("Select name");
            return;
        }
        if (TextUtils.isEmpty(placePhoneNum)){
            txtLocPhone.setError("enter a phone number");
            return;
        }
        if (placeName.length() < 4){
            txtEditLocName.setError("not less than 4 chars");
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
        map.put("place_latti" , latLng.latitude + "");
        map.put("place_langi" , latLng.longitude + "");

        // Pushing to database
        placeReference.child("my_places").push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    saveProgress.setVisibility(View.INVISIBLE);
                    // start main screen
                    startActivity(new Intent(SaveLocation.this , MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right , R.anim.slide_out_right);
                    finish();


                }else {
                    saveProgress.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void GetLocationDetails() {


        txtLocName.setText(place_data.place.getName());
        txtLocPosition.setText(place_data.place.getAddress());
        txtEditLocName.setText(place_data.place.getName());

        if (place_data.place.getWebsiteUri() != null){
            txtLocWeb.setText(place_data.place.getWebsiteUri().toString());
        }
        if (place_data.place.getPhoneNumber() != null){
            txtLocPhone.setText(place_data.place.getPhoneNumber().toString());
        }



    }
}

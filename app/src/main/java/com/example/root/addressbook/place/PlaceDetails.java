package com.example.root.addressbook.place;

import com.example.root.addressbook.place_data;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by root on 04/09/18.
 */

public class PlaceDetails {

    private String place_name;
    private String place_phone;
    private String place_web;
    private String place_desc;
    private String place_type;
    private String place_latti;
    private String place_langi;

    public PlaceDetails(){

    }

    public PlaceDetails(String place_name, String place_phone, String place_web, String place_desc, String place_type, String place_latti, String place_langi) {
        this.place_name = place_name;
        this.place_phone = place_phone;
        this.place_web = place_web;
        this.place_desc = place_desc;
        this.place_type = place_type;
        this.place_latti = place_latti;
        this.place_langi = place_langi;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPlace_phone() {
        return place_phone;
    }

    public void setPlace_phone(String place_phone) {
        this.place_phone = place_phone;
    }

    public String getPlace_web() {
        return place_web;
    }

    public void setPlace_web(String place_web) {
        this.place_web = place_web;
    }

    public String getPlace_desc() {
        return place_desc;
    }

    public void setPlace_desc(String place_desc) {
        this.place_desc = place_desc;
    }

    public String getPlace_type() {
        return place_type;
    }

    public void setPlace_type(String place_type) {
        this.place_type = place_type;
    }

    public String getPlace_latti() {
        return place_latti;
    }

    public void setPlace_latti(String place_latti) {
        this.place_latti = place_latti;
    }

    public String getPlace_langi() {
        return place_langi;
    }

    public void setPlace_langi(String place_langi) {
        this.place_langi = place_langi;
    }
}

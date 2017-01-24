package app.appurservice.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Place implements Serializable, ClusterItem {
    public int place_id;
    public String name;
    public String image;
    public String address;
    public String phone;
    public String website;
    public String description;
    public double lng;
    public double lat;
    public long last_update;

    @Override
    public LatLng getPosition() {
        return new LatLng(lat, lng);
    }

    public boolean isDraft(){
        return (address == null && phone == null && website == null && description == null);
    }

}

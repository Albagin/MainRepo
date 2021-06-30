package eviateam.evia.googlemaps;

import com.google.android.gms.maps.model.PolylineOptions;

public interface DirectionPointListener {
    public void onPath(PolylineOptions polyLine);
}
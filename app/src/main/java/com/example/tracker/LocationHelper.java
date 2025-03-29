package com.example.tracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationHelper {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    public LocationHelper(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getLocation(final OnLocationReceivedListener listener) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // Интервал обновления
        locationRequest.setFastestInterval(5000); // Наиболее быстрый интервал обновления

        if (ActivityCompat.checkSelfPermission(fusedLocationClient.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("LocationHelper", "Location permissions are not granted.");
            return; // Выход, если разрешения не предоставлены
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                int i=0;
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
//                        if ((Params.latitude != location.getLatitude()) || (Params.longitude != location.getLongitude()) || (i==0)) {
                            Params.latitude = location.getLatitude();
                            Params.longitude = location.getLongitude();
                            Log.d("LocationHelper", "Latitude: " + Params.latitude + ", Longitude: " + Params.longitude);
//                            listener.onLocationReceived(Params.latitude, Params.longitude);
                            i++;
//                        }
                    }
                }
                if (i>0) {
                    listener.onLocationReceived(Params.latitude, Params.longitude);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public interface OnLocationReceivedListener {
        void onLocationReceived(double latitude, double longitude);
    }
}

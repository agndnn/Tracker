package com.example.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.TextStyle;
import com.yandex.mapkit.mapview.MapView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class MapActivity extends AppCompatActivity {
    private MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);

        Params.mapkit.createUserLocationLayer(mapView.getMapWindow()).setVisible(true);
        LocationManager locManager = Params.mapkit.createLocationManager();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                locManager.requestSingleUpdate(new LocationListener() {
                    @Override
                    public void onLocationUpdated(@NonNull Location location) {
                        mapView.getMap().move(
                                new CameraPosition(location.getPosition(), 15.f, 0.f, 0.f),
                                new Animation(Animation.Type.SMOOTH, 1),
                                null
                        );
                    }
                    @Override
                    public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {}
                });
            }
        }, 500);

        Toast.makeText(getApplicationContext(), "Please wait for GPS calibration", Toast.LENGTH_SHORT).show();


        ArrayList<Pair<Point, String>> userLoc  = new ArrayList<>();

        for (User user: Params.usersOut) {

            new Thread(() -> {
                String latitude = "";
                String longitude = "";
                String name = "";
                try {
                    String url = Params.getCoordFromUrl(user.getCode(), user.getPhone());
                    Log.debug("url=" + url);
                    String response = (new HttpHelper()).sendGetRequest(url);

                    JSONObject jsonObject = new JSONObject(response);
                    int errc = jsonObject.getInt("errc");
                    if (errc == 0) {
                        JSONObject dataObject = jsonObject.getJSONObject("data");

                        latitude = dataObject.getString("latitude");
                        longitude = dataObject.getString("longitude");
                        name = dataObject.getString("name");

                        Log.debug("latitude=" + latitude + ", longitude=" + longitude);

                    } else {
                        String errm = jsonObject.getString("errm");
                        Log.debug("errm=" + errm);
                    }
                } catch (Exception e) {
                    Log.debug("http error");
                }

                Point mappoint = new Point(Double.parseDouble(latitude), Double.parseDouble(longitude));

                userLoc.add(Pair.create(mappoint, name));
            }).start();
        }

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Pair<Point, String> el: userLoc){
                    PlacemarkMapObject placemark = mapView.getMap().getMapObjects().addPlacemark(el.first);
                    placemark.setText(el.second, new TextStyle().setSize(4f).setColor(Color.BLACK).setPlacement(TextStyle.Placement.TOP));
                }
            }
        },5000);



        Button backButton = findViewById(R.id.btBack);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }
}
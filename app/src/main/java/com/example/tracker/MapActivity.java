package com.example.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;

import java.util.Objects;


public class MapActivity extends AppCompatActivity {
    private MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MapKitFactory.setApiKey("a4304081-4a62-4707-9204-65de6edc6562");
        //MapKitFactory.initialize(this);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_map);


        mapView = findViewById(R.id.mapView);
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("key_latitude",0);
        double longitude = intent.getDoubleExtra("key_longitude",0);
        Log.debug("MapActivity latitude="+latitude+", longitude="+longitude);
        //Toast.makeText(MapActivity.this, "latitude="+latitude+", longitude="+longitude, Toast.LENGTH_LONG).show();

        mapView.getMapWindow().getMap().move(
                new CameraPosition(new Point(latitude, longitude), 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 3),
                null);

/*
 // Create a custom marker with a custom icon
        BitmapDescriptor customIcon = BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_icon);
        Point point = new Point(latitude, longitude);
        mapView.getMap().getMapObjects().addPlacemark(point, customIcon);
 */
        Point mappoint= new Point(latitude, longitude);
        mapView.getMap().getMapObjects().addPlacemark(mappoint);
        //addCustomPlacemark(latitude, longitude);

        Button backButton = findViewById(R.id.btBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Завершаем текущую активность
            }
        });
        }
/*
    private void addCustomPlacemark(double latitude, double longitude) {
        Point mappoint = new Point(latitude, longitude);

        // Получите коллекцию объектов карты
        MapObjectCollection mapObjects = mapView.getMap().getMapObjects();

        // Загрузите ваше изображение из ресурсов
        Drawable drawable = getResources().getDrawable(R.drawable.your_custom_marker);

        // Создайте метку с кастомной иконкой
        PlacemarkMapObject placemark = mapObjects.addPlacemark(mappoint);

        // Можно настроить дополнительные параметры, например, задать размер, если это необходимо
        placemark.setIconStyle(new IconStyle()
                .setScale(1.5f) // Установите масштаб для увеличения размера иконки
                .setAnchor(new PointF(0.5f, 1))); // Установите привязку иконки (центр по горизонтали, низ по вертикали)
    }
*/
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
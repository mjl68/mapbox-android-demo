package com.mapbox.mapboxandroiddemo.examples.dds;

// #-code-snippet: animated-dash-line full-java

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mapbox.mapboxandroiddemo.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.net.MalformedURLException;
import java.net.URL;

import static com.mapbox.mapboxsdk.style.layers.Property.LINE_CAP_ROUND;
import static com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineDasharray;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

/**
 * Create an effect of animated and moving LineLayer dashes by rapidly adjusting the
 * dash and gap lengths.
 */
public class AnimatedDashLineActivity extends AppCompatActivity implements OnMapReadyCallback {

  private MapView mapView;
  private MapboxMap mapboxMap;
  private Handler handler;
  private String TAG = "AnimatedDashLine";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Mapbox access token is configured here. This needs to be called either in your application
    // object or in the same activity which contains the mapview.
    Mapbox.getInstance(this, getString(R.string.access_token));

    // This contains the MapView in XML and needs to be called after the access token is configured.
    setContentView(R.layout.activity_animated_dash_line);

    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(this);
  }

  @Override
  public void onMapReady(MapboxMap mapboxMap) {
    AnimatedDashLineActivity.this.mapboxMap = mapboxMap;
    initBikePathLayer();
    Log.d(TAG, "onMapReady: ");
  }

  private void initBikePathLayer() {
    try {
      GeoJsonSource geoJsonSource = new GeoJsonSource("animated_line_source", new URL(
        "https://raw.githubusercontent.com/Chicago/osd-bike-routes/master/data/Bikeroutes.geojson"
      ));
      mapboxMap.addSource(geoJsonSource);
      LineLayer animatedDashBikeLineLayer = new LineLayer("animated_line_layer_id", "animated_line_source");
      animatedDashBikeLineLayer.withProperties(
        lineWidth(4.5f),
        lineColor(Color.parseColor("#bf42f4")),
        lineCap(LINE_CAP_ROUND),
        lineJoin(LINE_JOIN_ROUND)
      );
      mapboxMap.addLayer(animatedDashBikeLineLayer);
      Log.d(TAG, "initBikePathLayer: here");
      Runnable runnable = new RefreshDashAndGapRunnable(this.mapboxMap, new Handler());
      Log.d(TAG, "initBikePathLayer: runnable made");
      handler.postDelayed(runnable, 25);
      Log.d(TAG, "initBikePathLayer: postDelayed");
    } catch (MalformedURLException malformedUrlException) {
      Log.d("AnimatedDashLine", "Check the URL: " + malformedUrlException.getMessage());
    }
  }

  private static class RefreshDashAndGapRunnable implements Runnable {
    private float t, a, b, c, d;
    private float dashLength = 1;
    private float gapLength = 3;
    // We divide the animation up into 40 steps to make careful use of the finite space in
    // LineAtlas
    private float steps = 40;
    // A # of steps proportional to the dashLength are devoted to manipulating the dash
    private float dashSteps = steps * dashLength / (gapLength + dashLength);
    // A # of steps proportional to the gapLength are devoted to manipulating the gap
    private float gapSteps = steps - dashSteps;

    // The current step #
    private int step = 0;
    private MapboxMap mapboxMap;
    private Handler handler;
    private String TAG = "AnimatedDashLine";

    RefreshDashAndGapRunnable(MapboxMap mapboxMap, Handler handler) {
      this.mapboxMap = mapboxMap;
      this.handler = handler;
      Log.d(TAG, "RefreshDashAndGapRunnable: finished");
    }

    @Override
    public void run() {
      Log.d(TAG, "run: ");
      step = step + 1;
      if (step >= steps) {
        step = 0;
      }
      if (step < dashSteps) {
        t = step / dashSteps;
        a = (1 - t) * dashLength;
        b = gapLength;
        c = t * dashLength;
        d = 0;
      } else {
        t = (step - dashSteps) / (gapSteps);
        a = 0;
        b = (1 - t) * gapLength;
        c = dashLength;
        d = t * gapLength;
      }
      Log.d(TAG, "run: here");
      mapboxMap.getLayer("animated_line_layer_id").setProperties(
        lineDasharray(new Float[] {a, b, c, d})
      );
      Log.d(TAG, "run: layer done being gotten");
      handler.postDelayed(this, 25);
    }
  }

  // Add the mapView lifecycle to the activity's lifecycle methods
  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  protected void onStart() {
    super.onStart();
    mapView.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mapView.onStop();
  }

  @Override
  public void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }
}
// #-end-code-snippet: animated-dash-line full-java
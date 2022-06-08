package com.nirmalbhetwal.lab1_nirmal_c0841296_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.nirmalbhetwal.lab1_nirmal_c0841296_android.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String TAG = "Maps Activity";
    private static final String TAG_LINE1 = "LINE_1";
    private static final String TAG_LINE2 = "LINE_2";
    private static final String TAG_LINE3 = "LINE_3";
    private static final String TAG_LINE4 = "LINE_4";
    private static final String TAG_CENTER = "CENTER";

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private final int LOCATION_REQUEST_CODE = 1;
    private int quadrilateralIndex = 65;
    private int lineIndex = 97;
    Polygon polygon;

    // initialize the fused location provider client
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private LocationCallback locationCallback;
    private final int REQUEST_CODE = 1;
    private final int UPDATE_INTERVAL = 5000;
    private final int FASTEST_UPDATE_INTERVAL = 3000;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int pickedColor;

    private List<String> permissionsToRequest;
    private List<String> permissions = new ArrayList<>();
    private List<String> permissionsRejected = new ArrayList<>();

    Polyline polyline1, clickedPolyLine;
    Polyline polyline2;
    Polyline polyline3;
    Polyline polyline4;
    LatLngBounds.Builder boundBuilder;
    LatLngBounds bounds;
    LatLng toronto = new LatLng(43.65, -79.38);
    LatLng brampton = new LatLng(43.7315, -79.7624);
    LatLng mississauga = new LatLng(43.5890, -79.6441);
    LatLng vaughan = new LatLng(43.8563, -79.5085);

    private Marker centerPolygonMarker = null;
    private ArrayList<Marker> mMarkers = new ArrayList<>();
    private ArrayList<Marker> mMidpointMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = permissionsToRequest(permissions);

        if (permissionsToRequest.size() > 0) {
            requestPermissions(permissionsToRequest.toArray(
                    new String[permissionsToRequest.size()]
                    ),
                    REQUEST_CODE);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        displayMarkersInMap();

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(@NonNull Polygon polygon) {
                if (centerPolygonMarker == null) {
                    centerPolygonMarker = mMap.addMarker(new MarkerOptions().position(bounds.getCenter()).title(String.format("Total distance: %.2f", getTotalDistanceOfPolygon())).snippet("Click here to change color"));
                    centerPolygonMarker.setTag(TAG_CENTER);
                    centerPolygonMarker.showInfoWindow();
                } else {
                    centerPolygonMarker.remove();
                    centerPolygonMarker = null;
                }
            }

            private double getTotalDistanceOfPolygon() {
                return 0;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker clickedMarker) {
                for (Marker marker : mMarkers) {
                    if (clickedMarker.getTag().equals(marker.getTag())) {
                        return;
                    }
                }
                pickedColor = 0;
                Dialog dialog = new Dialog(MapsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.color_picker);
                int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
                dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                Button setColor = dialog.findViewById(R.id.btnSetColor);
                Button cancel = dialog.findViewById(R.id.btnCancel);

                dialog.show();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                setColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText colorValue = dialog.findViewById(R.id.colorValue);
                        String colorText = colorValue.getText().toString();

                        pickedColor = Color.parseColor(colorText);
                        dialog.dismiss();

                        switch (clickedMarker.getTag().toString()) {
                            case TAG_CENTER:
                                polygon.setFillColor(pickedColor);
                                break;
                            default:
                                if (clickedPolyLine != null) {
                                    clickedPolyLine.setColor(pickedColor);
                                }
                                break;
                        }
                    }
                });
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                // set the title of marker and update marker
                String tag = String.format("%c", quadrilateralIndex++);
                Marker marker  = mMap.addMarker(new MarkerOptions().position(latLng).title(tag));
                mMarkers.add(marker);
                marker.showInfoWindow();
                marker.setTag(tag);

                mMarkers = convexHull(mMarkers);
                mMap.clear();
                drawPolygon();
                drawPolyLine();
                calculateBounds();

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                mMap.clear();
                mMarkers = new ArrayList<>();
                mMidpointMarkers = new ArrayList<>();
                quadrilateralIndex = 65;
                lineIndex = 97;
            }
        });
    }

    private void displayMarkersInMap() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

//        String markerTag = String.format("%c", quadrilateralIndex++);
//        Marker marker = new Marker(new zzx);
//
//        Marker m = mMap.addMarker(new MarkerOptions().position(toronto).title(markerTag));
//        m.setTag(markerTag);
//        mMarkers.add(m);
//        markerTag = String.format("%c", quadrilateralIndex++);
//        m = mMap.addMarker(new MarkerOptions().position(mississauga).title(markerTag));
//        m.setTag(markerTag);
//        m.setSnippet("Click here to change line color");
//        mMarkers.add(m);
//        markerTag = String.format("%c", quadrilateralIndex++);
//        m = mMap.addMarker(new MarkerOptions().position(brampton).title(markerTag));
//        m.setTag(markerTag);
//        m.setSnippet("Click here to change line color");
//        mMarkers.add(m);
//        markerTag = String.format("%c", quadrilateralIndex++);
//        m = mMap.addMarker(new MarkerOptions().position(vaughan).title(markerTag));
//        m.setTag(markerTag);
//        m.setSnippet("Click here to change line color");
//        mMarkers.add(m);
        boundBuilder = new LatLngBounds.Builder();
        if (mMarkers.size() == 0) {
            boundBuilder.include(toronto);
            boundBuilder.include(mississauga);
            boundBuilder.include(brampton);
        }

        bounds = boundBuilder.build();
        calculateBounds();
    }

    private void calculateBounds() {
        for (Marker markerLocation : mMarkers) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLocation.getPosition()));
            boundBuilder.include(markerLocation.getPosition());
        }

        if (mMarkers.size() == 0) {
            boundBuilder.include(toronto);
        }

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10);
        bounds = boundBuilder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,  width, height, padding);
        mMap.animateCamera(cameraUpdate);
    }

    private void drawPolygon() {
        ArrayList<LatLng> latLngs = new ArrayList<>();

        for (Marker marker : mMarkers) {
            latLngs.add(marker.getPosition());
        }
        polygon = mMap.addPolygon(new PolygonOptions().clickable(true).addAll(latLngs));
        polygon.setStrokeColor(Color.RED);
        polygon.setTag("alpha");
        // set alpha to 35% = 0x59
        // color code = 0xAARRGGBB
        int greenColor = 0x5900ff00;
        polygon.setFillColor(greenColor);
    }

    private void drawPolyLine () {
        for (int i = 0; i < mMarkers.size(); i++) {
            if (i < mMarkers.size() - 1) {
                LatLng point1 = mMarkers.get(i).getPosition();
                LatLng point2 = mMarkers.get(i+1).getPosition();

                PolylineOptions line = new PolylineOptions().add(point1, point2)
                    .width(5)
                    .color(Color.RED);
                line.clickable(true);
                clickedPolyLine = mMap.addPolyline(line);
                clickedPolyLine.setTag(String.format("%c", lineIndex++));
            } else if (i == mMarkers.size() - 1) {
                LatLng point1 = mMarkers.get(i).getPosition();
                LatLng point2 = mMarkers.get(0).getPosition();

                PolylineOptions line = new PolylineOptions().add(point1, point2)
                        .width(5)
                        .color(Color.RED);
                line.clickable(true);
                clickedPolyLine = mMap.addPolyline(line);
                clickedPolyLine.setTag(String.format("%c", lineIndex++));
            }
        }

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {
                Log.d(TAG, "polyline click");
                clickedPolyLine = polyline;
                List<LatLng> points = polyline.getPoints();
                LatLng point1 = points.get(0);
                LatLng point2 = points.get(1);

                LatLng midpoint = new LatLng((point1.latitude + point2.latitude)/2, (point1.longitude + point2.longitude)/2);
                float[] results = {0};
                Location.distanceBetween(point1.longitude, point2.latitude, point2.latitude, point2.longitude, results);
                MarkerOptions markerOptions = new MarkerOptions().position(midpoint).title(String.format("Distance: %.2f", results[0])).snippet("Click here to change line color");
                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(String.format("%c", lineIndex++));
                marker.showInfoWindow();

                if (isMarkerAlreadyPlaced(polyline.getTag().toString())) {
                    removeMarkerOnLine(marker, polyline.getTag().toString());
                } else {
                    marker.setTag(polyline.getTag().toString());
                    mMidpointMarkers.add(marker);
                }
            }

            private boolean isMarkerAlreadyPlaced(String tag) {
                boolean exists = false;

                // check if the marker is already placed
                for (Marker marker : mMidpointMarkers) {
                    if (marker.getTag().equals(tag)) {
                        exists = true;
                        break;
                    }
                }

                return exists;
            }
        });
    }

    private void removeMarkerOnLine(Marker marker, String tag) {
        // remove the midpoint parker on the line
        for (int i = 0; i < mMidpointMarkers.size(); i++)  {
            if (mMidpointMarkers.get(i).getTag().equals(tag)) {
                mMidpointMarkers.get(i).remove();
                mMidpointMarkers.remove(i);
                marker.remove();
                break;
            }
        }
    }

    private void startUpdatingLocation() {
        // location request
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                String address = "";
                try {
                    Geocoder geo = new Geocoder(MapsActivity.this.getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (!addresses.isEmpty()) {
                        if (addresses.size() > 0) {
                            address = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                            //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace(); // getFromLocation() may sometimes fail
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
        );
    }

    private List<String> permissionsToRequest(List<String> permissions) {
        ArrayList<String> results = new ArrayList<>();

        for (String permission : permissions) {
            if (!isGranted(permission)) {
                results.add(permission);
            }
        }

        return results;
    }

    private boolean isGranted(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // TODO: 2022-06-01 implement me
        if (requestCode == REQUEST_CODE) {
            for (String permission : permissions) {
                if (!isGranted(permission)) {
                    permissionsRejected.add(permission);
                }
            }

            if (permissionsRejected.size() > 0 ) {
                for (String rejectedPermission : permissionsRejected) {
                    if (shouldShowRequestPermissionRationale(rejectedPermission)) {
                        new AlertDialog.Builder(this)
                                .setMessage("Location access is mandatory")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        requestPermissions(new String[]{rejectedPermission}, REQUEST_CODE);
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .create()
                                .show();
                    }
                }
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            findLocation();
        }
    }
    private void findLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null) {
                    return;
                }
            }
        });

        startUpdatingLocation();
    }

    public ArrayList<Marker> convexHull(ArrayList<Marker> markers)
    {
        Projection projection = mMap.getProjection();

        int n = markers.size();
        if (n <= 3) return markers;

        ArrayList<Integer> next = new ArrayList<>();

        // find the leftmost point
        int leftMost = 0;
        Point point, leftmostPoint;
        Marker leftmostMarker = markers.get(leftMost);
        for (int i = 1; i < n; i++) {
            Marker marker = markers.get(i);
            point = projection.toScreenLocation(marker.getPosition());
            leftmostPoint = projection.toScreenLocation(leftmostMarker.getPosition());

            if (point.x < leftmostPoint.x) {
                leftMost = i;
            }
        }
        int p = leftMost, q;
        next.add(p);

        // iterate till p becomes leftMost
        do {
            q = (p + 1) % n;
            Marker marker = markers.get(q);
            leftmostMarker = markers.get(leftMost);
            point = projection.toScreenLocation(marker.getPosition());
            leftmostPoint = projection.toScreenLocation(leftmostMarker.getPosition());

            for (int i = 0; i < n; i++) {
                if (CCW(projection.toScreenLocation(markers.get(p).getPosition()), projection.toScreenLocation(markers.get(i).getPosition()), projection.toScreenLocation(markers.get(q).getPosition()))) {
                    q = i;
                }
            }
            next.add(q);
            p = q;
        } while (p != leftMost);

        ArrayList<Marker> convexHullPoints = new ArrayList();
        for (int i = 0; i < next.size() - 1; i++) {
            int ix = next.get(i);
            convexHullPoints.add(markers.get(ix));
        }

        return convexHullPoints;
    }

    private boolean CCW(Point p, Point q, Point r) {
        return (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y) > 0;
    }
}
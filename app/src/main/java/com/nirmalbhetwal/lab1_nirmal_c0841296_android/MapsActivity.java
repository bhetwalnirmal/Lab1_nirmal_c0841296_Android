package com.nirmalbhetwal.lab1_nirmal_c0841296_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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
import com.google.android.gms.maps.SupportMapFragment;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String TAG = "Maps Activity";
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private final int LOCATION_REQUEST_CODE = 1;

    // initialize the fused location provider client
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private LocationCallback locationCallback;
    private final int REQUEST_CODE = 1;
    private final int UPDATE_INTERVAL = 5000;
    private final int FASTEST_UPDATE_INTERVAL = 3000;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private List<String> permissionsToRequest;
    private List<String> permissions = new ArrayList<>();
    private List<String> permissionsRejected = new ArrayList<>();

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

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng toronto = new LatLng(43.65, -79.38);
        LatLng brampton = new LatLng(43.7315, -79.7624);
        LatLng mississauga = new LatLng(43.5890, -79.6441);
        LatLng vaughan = new LatLng(43.8563, -79.5085);
        mMap.addMarker(new MarkerOptions().position(toronto).title("Marker in Toronto").snippet("A"));
        mMap.addMarker(new MarkerOptions().position(brampton).title("Marker in Brampton").snippet("B"));
        mMap.addMarker(new MarkerOptions().position(mississauga).title("Marker in Mississauga").snippet("C"));
        mMap.addMarker(new MarkerOptions().position(vaughan).title("D"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(toronto));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(brampton));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mississauga));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(vaughan));

        LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();

        ArrayList<LatLng> markers = new ArrayList<LatLng>();
        markers.add(toronto);
        markers.add(brampton);
        markers.add(mississauga);
        markers.add(vaughan);

        for (LatLng marker : markers) {
            boundBuilder.include(marker);
        }
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10);
        LatLngBounds bounds = boundBuilder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,  width, height, padding);
        mMap.animateCamera(cameraUpdate);

        PolylineOptions line1 = new PolylineOptions().add(toronto, mississauga)
                .width(5)
                .color(Color.RED);
        PolylineOptions line2 = new PolylineOptions().add(mississauga, brampton)
                .width(5)
                .color(Color.RED);
        PolylineOptions line3 = new PolylineOptions().add(brampton, vaughan)
                .width(5)
                .color(Color.RED);
        PolylineOptions line4 = new PolylineOptions().add(vaughan, toronto)
                .width(5)
                .color(Color.RED);

        line1.clickable(true);
        line2.clickable(true);
        line3.clickable(true);
        line4.clickable(true);
        mMap.addPolyline(line1);
        mMap.addPolyline(line2);
        mMap.addPolyline(line3);
        mMap.addPolyline(line4);

        Polygon polygon = googleMap.addPolygon(new PolygonOptions().clickable(true).add(
                toronto,
                mississauga,
                brampton,
                vaughan
        ));
        polygon.setStrokeColor(Color.RED);
        polygon.setTag("alpha");
        polygon.setFillColor(Color.argb(90, 0, 100, 0));

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {
                List<LatLng> points = polyline.getPoints();
                LatLng point1 = points.get(0);
                LatLng point2 = points.get(1);

                LatLng midpoint = new LatLng((point1.latitude + point2.latitude)/2, (point1.longitude + point2.longitude)/2);
                float[] results = {0};
                Location.distanceBetween(point1.longitude, point2.latitude, point2.latitude, point2.longitude, results);
                Toast.makeText(MapsActivity.this, String.format("Distance between two points: %.2f", results[0]), Toast.LENGTH_LONG).show();
                mMap.addMarker(new MarkerOptions().position(midpoint).title("test"));
            }
        });

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(@NonNull Polygon polygon) {
                Log.d(TAG, " " + polygon.getTag());
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                Log.d(TAG, "lat: " + latLng.latitude);
            }
        });
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
//                tvLocationData.setText(
//                        String.format(
//                                "Latitude: %s\nLongitude: %s\nAltitude: %s\nAccuracy: %s\nAddress: %s\n",
//                                location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy(), address)
//                );
                Log.d(TAG, "on Success + Loc " + location.getLatitude() + " long: " + location.getLongitude());
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

                Log.d(TAG, "on Success " + location.getLatitude() + " " + location.getLongitude());
            }
        });

        startUpdatingLocation();
    }
}
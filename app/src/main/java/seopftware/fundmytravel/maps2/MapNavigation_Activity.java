package seopftware.fundmytravel.maps2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.maps2.module.DirectionFinder;
import seopftware.fundmytravel.maps2.module.DirectionFinderListener;
import seopftware.fundmytravel.maps2.module.Routes;

public class MapNavigation_Activity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private static final String TAG = "all_" + MapNavigation_Activity.class;

    // 구글맵 선언 변수
    private GoogleMap mMap;

    // 폴리라인을 그려주기 위한 변수들
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private LocationManager locationManager;

    double Current_latitude, Current_longtitude;

    private String address_start, address_finish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_navi);

        Intent intent = getIntent();
        address_start = intent.getStringExtra("address_start");
        address_finish = intent.getStringExtra("address_finish");



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        sendRequest();
    }

    private void sendRequest() {


        try {
            new DirectionFinder(this, address_start, address_finish).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng hcmus = new LatLng(37.566535,126.977969);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 12)); // 숫자가 커질수록 zoom이 확대됨


        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (ActivityCompat.checkSelfPermission(MapNavigation_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapNavigation_Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }

                // Check the network provided is enabled
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10000, new LocationListener() { // 최소 업데이트 변경 거리 및 시간
                        @Override
                        public void onLocationChanged(Location location) {
                            Current_latitude = location.getLatitude();
                            Current_longtitude = location.getLongitude();

                            LatLng latLng = new LatLng(Current_latitude, Current_longtitude); // Instantiate the class, Geocoder

                            Log.d("NETWORK상 현재 위치", String.valueOf(latLng));

                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.KOREA);
                            try {

                                List<Address> addressList = geocoder.getFromLocation(Current_latitude, Current_longtitude, 1);
                                String str = addressList.get(0).getAdminArea()+" "+addressList.get(0).getLocality() +" "+addressList.get(0).getThoroughfare() +" "+ addressList.get(0).getFeatureName();
                                str += "";
//                                mMap.addMarker(new MarkerOptions().position(latLng).title(str)); // 현재 나의 위치에 마커 추가

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    });
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() { // 최소 업데이트 변경 거리 및 시간
                        @Override
                        public void onLocationChanged(Location location) {
                            double latitude = location.getLatitude();
                            double longtitude = location.getLongitude();

                            LatLng latLng = new LatLng(latitude, longtitude); // Instantiate the class, Geocoder
                            Log.d("GPS상 현재 위치", String.valueOf(latLng));

                            Geocoder geocoder = new Geocoder(getApplicationContext());
                            try {
                                List<Address> addressList = geocoder.getFromLocation(latitude, longtitude, 1);
                                String str = addressList.get(0).getLocality() + ", ";
                                str += addressList.get(0).getCountryName();
                                mMap.addMarker(new MarkerOptions().position(latLng).title(str));

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });

                }
                return false;
            }
        });

        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.", "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) { // 폴리라인 없애기. 이 함수가 사용되어지지 않으면 폴리라인은 맵에서 없어지지 않는다.
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Routes> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Routes route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

            originMarkers.add(mMap.addMarker(new MarkerOptions() // 시작 지역에 마커 추가
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title("출발지") // 마커 클릭 시 정보창에 표시되는 문자열
//                    .snippet("") // 제목 아래에 표시되는 추가 텍스트
                    .position(route.startLocation))); // 위치 (Latlng 값) - 필수값
            destinationMarkers.add(mMap.addMarker(new MarkerOptions() // 목적지 지역에 마커 추가
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title("목적지")
//                    .snippet("출장 장소 이름")
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++) // 폴리라인의 각 포인터 지점 추가하기
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions)); // 폴리 라인 옵션 추가
        }
    }

}

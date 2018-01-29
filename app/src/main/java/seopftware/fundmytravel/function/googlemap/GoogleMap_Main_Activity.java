//package seopftware.fundmytravel.function.googlemap;
//
//import android.app.FragmentManager;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapFragment;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.maps.android.clustering.ClusterManager;
//
//import java.util.Random;
//
//import seopftware.fundmytravel.R;
//
//public class GoogleMap_Main_Activity extends FragmentActivity implements OnMapReadyCallback {
//
//    private static final String TAG = "all_"+"GoogleMap_Activity";
//
//
////    private ClusterManager<Person> mClusterManager;
////    private Random mRandom = new Random(1984);
//
//    private GoogleMap mMap;
//    private Random mRandom = new Random(1984);
//
//
//    // 구글맵 초기 시작 위치
//    private static final double SEOUL_LAT = 37.5449546;
//    private static final double SEOUL_LNG = 126.9647997;
//    private static final LatLng Seoul = new LatLng(SEOUL_LAT, SEOUL_LNG);
//
//    // Cluster Manager
//    ClusterManager<Person> mClusterManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_google_map_main);
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        FragmentManager fragmentManager = getFragmentManager();
//        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//    }
//
//
//    // GoogleMap 사용 준비가 되었을 때 호출되는 메서드
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
///*        LatLng SEOUL = new LatLng(37.56, 126.97);
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(SEOUL);
//        markerOptions.title("서울");
//        markerOptions.snippet("한국의 수도");
//        map.addMarker(markerOptions);
//        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
//        map.animateCamera(CameraUpdateFactory.zoomTo(10));*/
//
//
//        mMap = googleMap;
//
//        mMap.addMarker(new MarkerOptions().position(Seoul));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Seoul, 14.0f));
//
//        // 클러스터 매니저를 생성
//        mClusterManager = new ClusterManager<>(this, mMap);
//        googleMap.setOnCameraIdleListener(mClusterManager);
//        googleMap.setOnMarkerClickListener(mClusterManager);
//        googleMap.setOnInfoWindowClickListener(mClusterManager);
//
//        addAddress();
//
////        //setOnCameraChangeListener => CameraMoveStartedListener, CameraMoveListener, OnCameraIdleListener
////        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
////            @Override
////            public void onCameraMoveStarted(int i) {
////                Log.d(TAG, "i 값은 뭘까? : " + i);
////
////
////
////            }
////        });
//    }
//
//    private LatLng position() {
//        return new LatLng(random(51.6723432, 51.38494009999999), random(0.148271, -0.3514683));
//    }
//
//    private double random(double min, double max) {
//        return mRandom.nextDouble() * (max - min) + min;
//    }
//
//    private void addAddress() {
//
//        for(int j=0; j<10; j++) {
////            double lat = SEOUL_LAT + (i / 200d);
////            double lng = SEOUL_LNG + (i / 200d);
//
//
////            mClusterManager.addItem(new Person(new LatLng(lat, lng), "PJ", R.drawable.kakao1));
//            mClusterManager.addItem(new Person(position(), "PJ", R.drawable.kakao1));
//        }
//    }
//}

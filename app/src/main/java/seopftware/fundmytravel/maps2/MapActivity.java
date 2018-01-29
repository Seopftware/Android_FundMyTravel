//package seopftware.fundmytravel.maps2;
//
//import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.maps.android.clustering.Cluster;
//import com.google.maps.android.clustering.ClusterItem;
//import com.google.maps.android.clustering.ClusterManager;
//import com.google.maps.android.clustering.view.DefaultClusterRenderer;
//import com.google.maps.android.ui.IconGenerator;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import seopftware.fundmytravel.R;
//import seopftware.fundmytravel.function.googlemap.MultiDrawable;
//
//public class MapActivity extends BaseGoogleMapsActivity {
//
//    private GoogleMap mMap;
//    private ClusterManager<Person2> mClusterManager;
//    private static final String TAG = "all_"+ "MapActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onMapReady(final GoogleMap googleMap) {
//        setupMap(googleMap);
//
//        mMap = googleMap;
//
//        // 초기 카메라 위치
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-26.167616, 28.079329), 9.5f));
//
//        // 클러스터링 매니저
//        // 클러스터링을 할 때는 구글맵에 직접 마커를 찍어 주는 것이 아니라, ClusterManager
//        mClusterManager = new ClusterManager<>(this, googleMap);
//        mClusterManager.setRenderer(new RenderClusterInfoWindow());
//        googleMap.setOnCameraIdleListener(mClusterManager);
//        googleMap.setOnMarkerClickListener(mClusterManager);
//        googleMap.setOnInfoWindowClickListener(mClusterManager);
//        addPersonItems(); // 마커 추가하는 함수
//
//
//        // Cluster 마커를 클릭했을 때
//        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Person2>() {
//            @Override
//            public boolean onClusterClick(Cluster<Person2> cluster) {
//
//                Toast.makeText(getApplicationContext(), "setOnClusterClickListener111", Toast.LENGTH_LONG).show();
//
//                // Show a toast with some info when the cluster is clicked.
//
//                for(int i=0; i<=cluster.getSize(); i++) {
//
//                    String firstName = cluster.getItems().iterator().next().getName();
//
//                    Log.d(TAG, "name of people : " + firstName);
////                    Toast.makeText(getApplicationContext(), cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();
//
//                }
//
//                // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
//                // inside of bounds, then animate to center of the bounds.
//
//                // Create the builder to collect all essential cluster items for the bounds.
//                LatLngBounds.Builder builder = LatLngBounds.builder();
//                for (ClusterItem item : cluster.getItems()) {
//                    builder.include(item.getPosition());
//                }
//
//                // Get the LatLngBounds
//                final LatLngBounds bounds = builder.build();
//
//                // Animate camera to the bounds
//                try {
//                    getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                return true;
//            }
//        });
//
//        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Person2>() {
//            @Override
//            public boolean onClusterItemClick(Person2 person2) {
//                Toast.makeText(getApplicationContext(), "setOnClusterItemClickListener222", Toast.LENGTH_LONG).show();
//
//                return false;
//            }
//        });
//
//
//        mClusterManager.cluster();
//
//
//    }
//
//    private void addPersonItems() {
//        for (int i = 0; i < 1; i++) {
//            mClusterManager.addItem(new Person2(-26.187616, 20.079329, "1", R.drawable.profile_background));
//            mClusterManager.addItem(new Person2(-26.187616, 21.079329, "2", R.drawable.profile_background_2));
//            mClusterManager.addItem(new Person2(-26.187616, 23.079329, "3", R.drawable.profile_background_3));
//            mClusterManager.addItem(new Person2(-26.187616, 22.099329, "4", R.drawable.kakao1));
//            mClusterManager.addItem(new Person2(-26.187616, 22.109329, "5", R.drawable.kakao1));
//            mClusterManager.addItem(new Person2(-26.187616, 22.119329, "6", R.drawable.kakao1));
//            mClusterManager.addItem(new Person2(-26.187616, 22.069329, "7", R.drawable.kakao1));
//        }
//    }
//
//
///*
//     클러스터링은 크게 알고리즘과 렌더러 두 가지로 나눠져있다. 알고리즘은 클러스터를 어떤 위치에 생성할지, 어떤 마커를 어떤 클러스터에 넣을지를 연산한다.
//    렌더러는 클러스터를 이쁘게 출력해주는 역할을 한다.
//    알고리즘과 렌더러는 ClusterManager의 setAlgorithm과 setRenderer 메서드로 커스텀 클래스를 등록할 수 있으며
//    등록하지 않은 경우에는 유틸리티 라이브러리에 기본으로 탑재되어 있는 DefaultClusterRenderer와 NonHierarchicalDistanceBasedAlgorithm 클래스를 사용한다.*/
//
//    // Render Event를 발생시키는 함수
//    private class RenderClusterInfoWindow extends DefaultClusterRenderer<Person2> {
//
//        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext()); // 아이콘 생성기
//        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext()); // 클러스터 아이콘 생성기
//        private ImageView mImageView; // 단일 아이콘 이미지뷰 (아이콘 -> 이미지뷰로 보이게 하기)
//        private ImageView mClusterImageView; // 클러스터링 됐을 때의 이미지 뷰
//        private int mDimension;
//
//
//        // 생성자
//        public RenderClusterInfoWindow() {
//            super(getApplicationContext(), getMap(), mClusterManager);
//
//            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
//            mClusterIconGenerator.setContentView(multiProfile);
//            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
//
//            mImageView = new ImageView(getApplicationContext());
//            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
//            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
//            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
//            mImageView.setPadding(padding, padding, padding, padding);
//            mIconGenerator.setContentView(mImageView);
//        }
//
//        @Override
//        protected void onClusterRendered(Cluster<Person2> cluster, Marker marker) {
//            super.onClusterRendered(cluster, marker);
//        }
//
//        // 클러스터 생성 전 각 아이템 하나하나 랜더러
//        @Override
//        protected void onBeforeClusterItemRendered(Person2 item, MarkerOptions markerOptions) {
//            // Draw a single person.
//            // Set the info window to show their name.
//            mImageView.setImageResource(item.getResourceId()); // 이미지뷰
//            Bitmap icon = mIconGenerator.makeIcon(); // 아이콘
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getName()); // 최종 마커 클릭 시 타이틀
//            super.onBeforeClusterItemRendered(item, markerOptions);
//        }
//
//        // 클러스터 생성 전 각 클러스터 렌더러
//        @Override
//        protected void onBeforeClusterRendered(Cluster<Person2> cluster, MarkerOptions markerOptions) {
//
//            // Draw multiple people.
//            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
//            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize())); // 사진 최대 4장 까지 보이게끔 (카카오톡 멀티방처럼)
//            int width = mDimension;
//            int height = mDimension;
//
//            for (Person2 p : cluster.getItems()) {
//                // Draw 4 at most.
//                if (profilePhotos.size() == 4) break; // 이미지 갯수가 4개 까지만 그려주고 그 이상은 break;
//
//                Drawable drawable = getResources().getDrawable(p.getResourceId()); // drawable resource 가져오기
//                drawable.setBounds(0, 0, width, height);
//                profilePhotos.add(drawable);
//            }
//
//            // 클러스터시 멀티 이미지
//            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
//            multiDrawable.setBounds(0, 0, width, height);
//            mClusterImageView.setImageDrawable(multiDrawable);
//            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
//
//        }
//
//        @Override
//        protected boolean shouldRenderAsCluster(Cluster cluster) {
//            // Always render clusters. 아이템이 1개 이상이면 무조건 클러스터링
//            return cluster.getSize() > 1;
//        }
//    }
//
//    protected GoogleMap getMap() {
//        return mMap;
//    }
//}

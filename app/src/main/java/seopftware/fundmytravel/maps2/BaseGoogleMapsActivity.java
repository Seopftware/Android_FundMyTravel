package seopftware.fundmytravel.maps2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.adapter.Roomlist_Recycler_Adapter;
import seopftware.fundmytravel.dataset.Parsing;
import seopftware.fundmytravel.dataset.Roomlist_Item;
import seopftware.fundmytravel.function.etc.RecyclerItemClickListener;
import seopftware.fundmytravel.function.googlemap.MultiDrawable;
import seopftware.fundmytravel.function.retrofit.HttpService;
import seopftware.fundmytravel.function.retrofit.RetrofitClient;

public class BaseGoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ClusterManager<Markers> mClusterManager;


    // Recycler View 관련 변수
    RecyclerView recyclerView;
    Roomlist_Recycler_Adapter adapter;
    ArrayList<Roomlist_Item> recycler_itemlist;
    Roomlist_Item recycler_item;

    // UI
    SlidingUpPanelLayout mLayout;
    TextView tv_numofroom; // 방송 갯수 목록 표시


    private static final String TAG = "all_" + "BaseGoogleMaps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_google_maps);

        //액션바 설정 부분
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        toolbar.bringToFront();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Maps" + "</font>"));

        // UI
        tv_numofroom = (TextView) findViewById(R.id.tv_numofroom); // 룸 갯수를 표시할 text View

        // Google Map
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // =========================================================================================================
        // SlidingUp layout
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setShadowHeight(0); // 하단에 보이는 그림자 없애기
        // =========================================================================================================

        // =========================================================================================================
        // recycler view
        // 옵션적용
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // 아이템 적용
        recycler_itemlist = new ArrayList<Roomlist_Item>();
        recycler_item = new Roomlist_Item();

        // 어뎁터 적용
        adapter = new Roomlist_Recycler_Adapter(recycler_itemlist);
        recyclerView.setAdapter(adapter);

        // 클릭 이벤트
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "방송 시청 화면으로...", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        // =========================================================================================================

        // 서버로 부터 방 정보 리스트 불러오기
        getMarkerItems();
    } // onCreate() finish


    // =========================================================================================================
    // 액션바 메뉴 부분
    // =========================================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.demo, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            // AR 길찾기
            case R.id.action_findway:
                Toast.makeText(getApplicationContext(), "길 찾기", Toast.LENGTH_LONG).show();
                break;

            //  하단의 panel layout 보이기 / 숨기기
            case R.id.action_toggle: {
                if (mLayout != null) {
                    if (mLayout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                        item.setTitle(R.string.action_show);
                    } else {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        item.setTitle(R.string.action_hide);
                    }
                }
                return true;
            }

            case R.id.action_anchor: {
                if (mLayout != null) {
                    if (mLayout.getAnchorPoint() == 1.0f) {
                        mLayout.setAnchorPoint(0.7f);
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                        item.setTitle(R.string.action_anchor_disable);
                    } else {
                        mLayout.setAnchorPoint(1.0f);
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        item.setTitle(R.string.action_anchor_enable);
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    } // onOptionsItemSelected finish

    // 빽키 클릭시
    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
    // =========================================================================================================


    // =========================================================================================================
    // 구글맵 관련 함수들
    // =========================================================================================================

    // 구글맵 UI 관련 설정 하는 곳
    protected void setupMap(GoogleMap googleMap) {
        if (googleMap != null) {
            return;
        }

        // 구글맵 UI 관련 옵션 조정하는 곳
        UiSettings mUiSettings = googleMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true); // 확대/축소 컨트롤
        mUiSettings.setCompassEnabled(false); // 나침반
        mUiSettings.setMyLocationButtonEnabled(true); // 나의 위치 클릭 버튼
        mUiSettings.setScrollGesturesEnabled(true); // 유저가 손가락으로 지도를 드래그하여 스크롤 할 수 있음.
        mUiSettings.setZoomGesturesEnabled(true); //
        mUiSettings.setTiltGesturesEnabled(true); // 사용자가 지도에 두 손가락을 대고 한꺼번에 위아래로 지도를 틸트할 수 있음.
        mUiSettings.setRotateGesturesEnabled(true); // 맵을 회전시킬 수 있음
        mUiSettings.setMapToolbarEnabled(false); // 지도 하단 오른쪽에 나타나는 노출 여부
        mUiSettings.setMyLocationButtonEnabled(true); // 내 위치를 나타내는 버튼을 표시 여부

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        setupMap(mMap); // 맵 관련 UI 설정

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
        mMap.setMyLocationEnabled(true);

        // 초기 카메라 위치 (내 위치로 바꾸기)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-26.167616, 22.079329), 3));

        // 클러스터링 매니저
        // 클러스터링을 할 때는 구글맵에 직접 마커를 찍어 주는 것이 아니라, ClusterManager
        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setRenderer(new RenderClusterInfoWindow());
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.cluster();

//        addPersonItems(); // 마커 추가하는 함수

         // Cluster 마커를 클릭했을 때
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Markers>() {
            @Override
            public boolean onClusterClick(Cluster<Markers> cluster) {

                Toast.makeText(getApplicationContext(), "클러스터 아이템 클릭!", Toast.LENGTH_LONG).show();

                // 클러스터링 클릭 시 Sliding Layout 보여주기 + 아이템 불러들인 다음 Recycler View에 적용시키기
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);


                recycler_itemlist.clear(); // recycler view 초기화 시키기
                tv_numofroom.setText("방송 목록 보기 (" + String.valueOf(cluster.getSize()) + ")");

                for (Markers markers : cluster.getItems()) {

                    String room_id = markers.getRoom_id();
                    int room_numpeople = markers.getRoom_numpeople();
                    String room_name_title = markers.getRoom_name_title();
                    String room_name_tag = markers.getRoom_name_tag();
                    String room_name_streamer = markers.getRoom_name_streamer();
                    String room_image_path = markers.getRoom_image_path();
                    String room_status = markers.getRoom_status();

                    adapter.addRoom(room_id, room_numpeople, room_name_title, room_name_tag, room_name_streamer, room_image_path, room_status);
                }

                // recycler view 업데이트
                adapter.notifyDataSetChanged();

                // max height 정해주기
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = 510;
                recyclerView.setLayoutParams(params);


                // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
                // inside of bounds, then animate to center of the bounds.
                // Create the builder to collect all essential cluster items for the bounds.
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (ClusterItem item : cluster.getItems()) {
                    builder.include(item.getPosition());
                }

                // Get the LatLngBounds
                final LatLngBounds bounds = builder.build();

                // Animate camera to the bounds
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });

        // 하나의 아이템을 클릭했을 때 나타나는 List View
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Markers>() {
            @Override
            public boolean onClusterItemClick(Markers person2) {
                Toast.makeText(getApplicationContext(), "하나의 아이템 클릭!", Toast.LENGTH_LONG).show();

                Log.d(TAG, "mLayout.getPanelState() :" + mLayout.getPanelState());


                recycler_itemlist.clear();
                adapter.addRoom("1", 1, "roomname", "room_tag", "streamer_name", "1.jpg", "live");
                adapter.notifyDataSetChanged();

                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = 255;
                recyclerView.setLayoutParams(params);

                return false;
            }
        });

    }

    // 서버로 부터 마커 아이템 정보들을 받아온다.
    private void getMarkerItems() {

        // Http 통신하는 부분
        // get_roomlist()
        Retrofit retrofit = RetrofitClient.getClient();
        HttpService httpService = retrofit.create(HttpService.class);
        Call<Parsing> comment = httpService.get_roomlist(1); // 변수를 하나도 보내지 않으면 안되기에 숫자 1 입력.
        comment.enqueue(new Callback<Parsing>() {
            @Override
            public void onResponse(Call<Parsing> call, Response<Parsing> response) {

                Parsing parsing = response.body();
                //Todo 여기 무한 반복 어떻게?
                // 맵에 marker 뿌려주는 곳
                for (int i=0; i<6; i++) {

                     // 1
                    String room_id = parsing.getRoomlist().get(i).getRoomId();

                    // 2
                    int room_numpeople = parsing.getRoomlist().get(i).getRoomNumpeople();

                    // 3
                    String room_name_title = parsing.getRoomlist().get(i).getRoomNameTitle();

                    // 4
                    String room_name_tag = parsing.getRoomlist().get(i).getRoomNameTag();

                    // 5
                    String room_name_streamer = parsing.getRoomlist().get(i).getRoomNameStreamer();

                    // 6
                    String room_image_path = parsing.getRoomlist().get(i).getRoomImagePath();

                    // 7
                    String room_status = parsing.getRoomlist().get(i).getRoomStatus();

                    // 8
                    String room_location = parsing.getRoomlist().get(i).getRoomLocation();
                    Log.d(TAG, "room_location :" + room_location);

                    String replace_location = room_location.replaceAll("\"", "");
                    Log.d(TAG, "replace_location :" + replace_location);

                    String[] location_split=replace_location.split("_"); // 123.166_44.155 (쪼개기)
                    String s_longitude = location_split[0]; // longitude (경도)
                    String s_latitude = location_split[1]; // latitude (위도)

                    Log.d(TAG, "s_longitude :" + s_longitude);
                    Log.d(TAG, "s_latitude :" + s_latitude);

                    double longitude = Double.parseDouble(s_longitude);
                    double latitude = Double.parseDouble(s_latitude);

                    mClusterManager.addItem(new Markers(longitude, latitude, room_name_streamer, R.drawable.kakao1,
                            room_id,room_numpeople, room_name_title, room_name_tag, room_image_path, room_status));
                }

                adapter.notifyDataSetChanged();
                Log.d(TAG, "마커 addItem 작동!!!");

            }

            @Override
            public void onFailure(Call<Parsing> call, Throwable t) {

            }
        });



    }

/*
     클러스터링은 크게 알고리즘과 렌더러 두 가지로 나눠져있다. 알고리즘은 클러스터를 어떤 위치에 생성할지, 어떤 마커를 어떤 클러스터에 넣을지를 연산한다.
    렌더러는 클러스터를 이쁘게 출력해주는 역할을 한다.
    알고리즘과 렌더러는 ClusterManager의 setAlgorithm과 setRenderer 메서드로 커스텀 클래스를 등록할 수 있으며
    등록하지 않은 경우에는 유틸리티 라이브러리에 기본으로 탑재되어 있는 DefaultClusterRenderer와 NonHierarchicalDistanceBasedAlgorithm 클래스를 사용한다.*/

    // Render Event를 발생시키는 함수
    private class RenderClusterInfoWindow extends DefaultClusterRenderer<Markers> {

        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext()); // 아이콘 생성기
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext()); // 클러스터 아이콘 생성기
        private ImageView mImageView; // 단일 아이콘 이미지뷰 (아이콘 -> 이미지뷰로 보이게 하기)
        private ImageView mClusterImageView; // 클러스터링 됐을 때의 이미지 뷰
        private int mDimension; // 구글맵의 아이템 크기


        // 생성자
        public RenderClusterInfoWindow() {
            super(getApplicationContext(), mMap, mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onClusterRendered(Cluster<Markers> cluster, Marker marker) {
            super.onClusterRendered(cluster, marker);
        }


        // 클러스터 생성 전 각 아이템 하나하나 랜더러(하나의 구글맵 클릭 시)
        @Override
        protected void onBeforeClusterItemRendered(Markers item, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageResource(item.getResourceId()); // 이미지뷰
            Bitmap icon = mIconGenerator.makeIcon(); // 아이콘
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getRoom_name_streamer()); // 최종 마커 클릭 시 타이틀
            super.onBeforeClusterItemRendered(item, markerOptions);
        }

        // 클러스터 생성 전 각 클러스터 렌더러
        @Override
        protected void onBeforeClusterRendered(Cluster<Markers> cluster, MarkerOptions markerOptions) {

            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize())); // 사진 최대 4장 까지 보이게끔 (카카오톡 멀티방처럼)
            int width = mDimension;
            int height = mDimension;

            for (Markers p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break; // 이미지 갯수가 4개 까지만 그려주고 그 이상은 break;

                Drawable drawable = getResources().getDrawable(p.getResourceId()); // drawable resource 가져오기
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }

            // 클러스터시 멀티 이미지
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);
            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters. 아이템이 1개 이상이면 무조건 클러스터링
            return cluster.getSize() > 1;
        }
    }

    protected GoogleMap getMap() {
        return mMap;
    }




    // =========================================================================================================
    // 생명주기 관련
    // =========================================================================================================

    @Override
    protected void onResume() {
        super.onResume();
/*        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
    }
}



/*
mLayout 옵션들
        mLayout.setShadowHeight(0); // 하단에 보이는 그림자 없애기
        초기 구글맵 작동 시에는 Sliding Layout 숨기기
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);

            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "setFadeOnClickListener 작동!!!");
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
*/

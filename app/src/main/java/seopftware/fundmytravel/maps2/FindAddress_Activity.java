package seopftware.fundmytravel.maps2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import seopftware.fundmytravel.R;

public class FindAddress_Activity extends AppCompatActivity {

    private static final String TAG = "all_" + FindAddress_Activity.class;
    private WebView browser;
    private EditText et_Origin; // 출발지 주소
    private EditText et_Destination; // 목적지 주소
    String address_start, address_finish;


    // 위치 정보를 받아오기 위한 google location
    LocationManager locationManager;
    double Current_longtitude; // 출발지의 경도
    double Current_latitude; // 출발지의 위도

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 풀 스크린 만들기
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없애기
        setContentView(R.layout.activity_find_address);

        // 액션바 설정 부분
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the Actionbar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true); // 타이틀 보이기
        actionBar.setDisplayShowCustomEnabled(true); // 커스텀 여부
        actionBar.setDisplayHomeAsUpEnabled(true); // 빽 버튼 표시하기
        actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Maps" + "</font>"));

        // location manager 선언
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                100,
                1,
                mLocationListener);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                100,
                1,
                mLocationListener);

        // UI 설정
        et_Origin = (EditText) findViewById(R.id.et_Origin);
        et_Origin.setText("위치 정보 받아오는 중");
        et_Destination = (EditText) findViewById(R.id.et_Destination);

        // Daum 주소 검색 띄우기
        browser= (WebView) findViewById(R.id.WebView);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "Android");
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                browser.loadUrl("javascript:sample2_execDaumPostcode();");
//                browser.loadUrl("http://cdn.rawgit.com/jolly73-df/DaumPostcodeExample/master/DaumPostcodeExample/app/src/main/assets/daum.html");


            }
        });
        browser.loadUrl("http://cdn.rawgit.com/jolly73-df/DaumPostcodeExample/master/DaumPostcodeExample/app/src/main/assets/daum.html");


    }

    // WebView를 띄워주고 아이템 클릭 시 받아오는 함수
    class MyJavaScriptInterface {

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(final String data) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "data : 여기 옵니까?" + data);
                            et_Destination.setText(data);
                            address_finish = et_Destination.getText().toString();

                        }
                    });
                }
            }).start();
        }
    }

    // =========================================================================================================
    // GoogleMap 현재 나의 위치 받아 오기
    // =========================================================================================================

    private final LocationListener mLocationListener = new LocationListener() {

        // 위치값이 갱신되면 이벤트 발생
        // 값은 Location 형태로 리턴
        @Override
        public void onLocationChanged(Location location) {

            Log.d(TAG, "onLocationChanged, location: " + location);
            // 위치정보를 통해 받아오는 값들
            Current_latitude = location.getLatitude(); // 위도
            Current_longtitude = location.getLongitude(); // 경도

            LatLng latLng = new LatLng(Current_latitude, Current_longtitude); // Instantiate the class, Geocoder
            Log.d("GPS상 현재 위치", String.valueOf(latLng));

            // Latlng => 좌표-> 주소
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.KOREA);
            try {
                List<Address> addressList = geocoder.getFromLocation(Current_latitude, Current_longtitude, 1);
                String str =
//                        addressList.get(0).getAdminArea()+" "
                        addressList.get(0).getLocality() +" "
                        +addressList.get(0).getThoroughfare();
//                        +addressList.get(0).getFeatureName();
//                str += addressList.get(0).getCountryName();

                Log.d(TAG, "Latlng -> Location : " + str);


                // 출발지 정보
                et_Origin.setText(str);
//                address_start = et_Origin.getText().toString();



            } catch (IOException e) {
                e.printStackTrace();
            }

            locationManager.removeUpdates(mLocationListener); // 위치 정보 받아오면 멈추기
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    // =========================================================================================================
    // 액션바 관련 함수들
    // =========================================================================================================

    // 액션바 메뉴 layout inflate  시키기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }

    // 액션바 아이템 클릭 구현
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // 빽키
            case android.R.id.home: {
                finish();
                return true;
            }


            // 체크 버튼
            case R.id.action_button: {

                // 만약 주소 입력칸이 비어있다면 입력 유도도

                if(et_Origin.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "출발지를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    et_Origin.requestFocus();
                    return true;
                }

               if(et_Destination.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "목적지를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                   et_Destination.requestFocus();
                    return true;
                }

                Log.d(TAG, "action button 클릭 후 address_start : " + et_Origin.getText().toString());

                // AR 길찾기 화면으로 목적지 주소 넘겨주기
                Intent intent=new Intent(getApplicationContext(), ARNavigation_Activity.class);
                intent.putExtra("address_start", et_Origin.getText().toString()); // 출발지 주소
                intent.putExtra("address_finish", address_finish); // 도착지 주소
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}

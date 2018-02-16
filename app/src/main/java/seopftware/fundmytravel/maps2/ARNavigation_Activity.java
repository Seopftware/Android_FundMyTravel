package seopftware.fundmytravel.maps2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.function.etc.CustomDialog_Activity;
import seopftware.fundmytravel.maps2.module.DirectionFinder;
import seopftware.fundmytravel.maps2.module.DirectionFinderListener;
import seopftware.fundmytravel.maps2.module.Routes;
import seopftware.fundmytravel.maps2.sensors.Orientation;
import seopftware.fundmytravel.maps2.sensors.OrientationSensorInterface;

public class ARNavigation_Activity extends CustomDialog_Activity
        implements DirectionFinderListener, SurfaceHolder.Callback, OrientationSensorInterface {

    private static final String TAG = "all_" + ARNavigation_Activity.class;

    // UI 관련 변수
    TextView tv_total_distance, tv_total_duration; // 도착지까지 남은 거리, 도착지까지 남은 시간
    private ProgressDialog progressDialog; // 경로 찾는 중에는 dialog 띄우기

    // SurfaceView
    SurfaceView cameraPreview;
    SurfaceHolder previewHolder; // SurfaceView를 관리하는 SurfaceHolder

    Camera mCamera; // 카메라 관련 모든 기능을 담당하는 객체
    boolean inPreview; // 미리보기 활성화 여부
    SensorManager sensorManager;

    float headingAngle;
    float pitchAngle;
    float rollAngle;

    int accelerometerSensor;
    float xAxis;
    float yAxis;
    float zAxis;

    float final_degree; // 최종 방향

    float finish_azimuth; // 두 지점 사이의 방위각

    LocationManager locationManager;

    // 경로 관련 변수
    String address_start, address_finish; // 경로 찾기 출발지 주소, 도착지 주소
    GoogleMap mMap;

    Double start_latitude, start_longitude; // 시작 지점의 위도/경도 -> 현재 내 위치의 위도/경도 값으로 바뀔 예정
    Double finish_latitude, finish_longitude; // 도착 지점의 위도.경도

    // 나의 폰 방향 센서 값을 가져올 변수
    Orientation orientationSensor;
    ImageView iv_phone, iv_final;
    TextView tv_phone, tv_final;
    TextView tv_destination_address, tv_CurrentLocation, tv_LeftDistance; // 도착지 주소, 현재 위치, 목적지까지 남은거리

    CountDownTimer countDownTimer;
    ImageButton ibtn_Navi; // polyline 그리는 구글맵으로 이동
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arnavigation);


        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        Intent intent = getIntent();
        address_start = intent.getStringExtra("address_start"); // 출발지
        address_finish = intent.getStringExtra("address_finish"); // 최종 목적지


        // Camera Preview 선언
        cameraPreview = (SurfaceView) findViewById(R.id.surfaceView);
        init(); // cameraview 선언

        // 서버로부터 받아온 정보 UI에 띄우기
        sendRequest();

        // Location Manager (현재 나의 위치를 받아오기 위한 변수
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location(); // 위치 정보를 받아오는 Provider(위치 제공) 등록

        // 폰의 자이로 방향을 알아내기 위한 함수
        orientationSensor = new Orientation(getApplicationContext(), this);

        //------Turn Orientation sensor ON-------
        // set tolerance for any directions
        orientationSensorOn();


        // UI 선언
        tv_destination_address= (TextView) findViewById(R.id.tv_destination_address); // 최종 목적지
        tv_CurrentLocation= (TextView) findViewById(R.id.tv_CurrentLocation); // 현재 위치 (위치 정보 받아오는 주기마다 업데이트)
        tv_LeftDistance= (TextView) findViewById(R.id.tv_LeftDistance); // 목적지까지 남은 거리

        tv_destination_address.setText(address_finish); // 목적지 주소 입력

        iv_phone = (ImageView) findViewById(R.id.iv_phone);
        iv_final = (ImageView) findViewById(R.id.iv_final);
        ibtn_Navi = (ImageButton) findViewById(R.id.ibtn_Navi);
        ibtn_Navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), MapNavigation_Activity.class);
                intent.putExtra("address_start", address_start); // 출발지
                intent.putExtra("address_finish", address_finish); // 도착지
                startActivity(intent);
            }
        });

        tv_phone= (TextView) findViewById(R.id.tv_phone);
        tv_final= (TextView) findViewById(R.id.tv_final);


    } // onCreate() finish


    // =========================================================================================================
    // 구글맵 관련
    // =========================================================================================================

    // 나의 위치를 받아오기 위한 리스너를 설정하는 곳
    private void location() {

        // Permission 허용 여부 체크
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
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
                10000, // GPS 갱신에 필요한 최소 시간 간격 1초(1000 milliseconds)
                10, // GPS 갱신에 필요한 최소 거리 (10m)
                mLocationListener);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                10000, // GPS 갱신에 필요한 최소 시간 간격 1초(1000 milliseconds)
                10, // GPS 갱신에 필요한 최소 거리 (10m)
                mLocationListener);

    }

    // 나의 현재 위치를 받아와서 갱신해주는 함수
    private final LocationListener mLocationListener = new LocationListener() {
        // 위치값이 갱신되면 이벤트 발생
        // 값은 Location 형태로 리턴
        @Override
        public void onLocationChanged(Location location) {
            // 위치정보를 통해 받아오는 값들
            String provider = location.getProvider(); // 위치 제공자 (GPS or Network)
            Log.d(TAG, "provider : " + provider);


            Log.d(TAG, "GPS_PROVIDER의 위치 정보를 받아옵니다.");
            start_latitude = location.getLatitude(); // 위도
            start_longitude = location.getLongitude(); // 경도


            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.KOREA);
            try {

                List<Address> addressList = geocoder.getFromLocation(start_latitude, start_longitude, 1);
                String str = addressList.get(0).getLocality() +" "+addressList.get(0).getThoroughfare() +" "+ addressList.get(0).getFeatureName();
                str += "";
                tv_CurrentLocation.setText("현재 위치: "+str);

            } catch (IOException e) {
                e.printStackTrace();
            }


            Log.d(TAG, "start_latitude : " + start_latitude);
            Log.d(TAG, "start_longitude : " + start_longitude);
            Log.d(TAG, "finish_latitude : " + finish_latitude);
            Log.d(TAG, "finish_longitude : " + finish_longitude);


            if(finish_latitude==null && finish_longitude==null) {
                return;
            } else {

                // 출발지와 도착지의 위/경도 값을 입력하면 두 지점의 값을 알 수 있다.
                finish_azimuth = getAZIMUTH(start_latitude, start_longitude, finish_latitude, finish_longitude);
                Log.d(TAG, "두 지점의 좌표를 통해 얻어낸 방위각 : " + finish_azimuth);


                // 두 점 사이의 거리를 float 값으로 리턴 받는다.
                float current_distance = getDistance(start_latitude, start_longitude, finish_latitude, finish_longitude);
                Log.d(TAG, "두 지점 사이의 거리 : " + String.valueOf(current_distance));


                String strNumber = String.format("%.1f", current_distance / 1000); // m 단위의 결과값을 km로 변경해주고, 소수점 첫째 자리까지만 표시
                tv_LeftDistance.setText("남은 거리: " + strNumber + " km"); // 목적지까지 남은 거리를 km 로 표시

                if(current_distance<=50) { // 남은 거리가 50m 이하일 경우 목적지 도착 알림

                    locationManager.removeUpdates(mLocationListener);
                    tv_LeftDistance.setText("남은 거리: 0 km"); // 목적지까지 남은 거리를 km 로 표시
                    arriveDialog();

                }
            }



            // double to int
//            int mDegree = (int) finish_azimuth;
            float mDegree = finish_azimuth;


//            // 이미지뷰 이미지 회전 시키기
            Matrix matrix = new Matrix();
            iv_final.setScaleType(ImageView.ScaleType.MATRIX);   //required
            matrix.postRotate((float) mDegree, 100, 100);
            iv_final.setImageMatrix(matrix);
            tv_final.setText("최종 목적지 방위각: " + mDegree);
//             ((ImageView) findViewById(R.id.iv_final)).setImageBitmap(rotateImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_navigation_white_24dp), mDegree));


//            delayLotation();

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


    // 구글맵 URL을 통해 경로 정보를 받아오기 위한 곳
    private void sendRequest() {
        try {
            new DirectionFinder(this, address_start, address_finish).execute(); // 출발 위치와 도착 위치 위/경도 값을 보낸 다음 구글로부터 경로값 받아옴.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    // DirectionFinderListener
    @Override
    public void onDirectionFinderStart() {
//        progressDialog = ProgressDialog.show(this, "Please wait.", "Finding direction..!", true);
        startProgress();


    }

    // 구글맵으로 네비 정보 받아오기에 성공하면 실행되는 함수
    @Override
    public void onDirectionFinderSuccess(List<Routes> routes) {

        for (Routes route : routes) {
            LatLng start_LatLng = route.startLocation;
            LatLng finish_LatLng = route.endLocation;


            start_latitude = start_LatLng.latitude;
            start_longitude = start_LatLng.longitude;

            finish_latitude = finish_LatLng.latitude;
            finish_longitude = finish_LatLng.longitude;

            Log.d(TAG, "start_LatLng(출발지 좌표) : " + start_LatLng); // 시작할 때의 위도/경도
            Log.d(TAG, "start_latitude(출발지의 위도) : " + start_latitude); // 시작할 때의 위도
            Log.d(TAG, "start_longitude(출발지의 경도) : " + start_longitude); // 시작할 때의 경도

            Log.d(TAG, "finish_LatLng(도착지 좌표) : " + finish_LatLng); // 도착했을 때의 위도/경도
            Log.d(TAG, "finish_latitude(도착지의 위도) : " + finish_latitude); // 도착했을 때의 위도
            Log.d(TAG, "finish_longitude(도착지의 경도) : " + finish_longitude); // 도착했을 때의 경도

            finish_azimuth = getAZIMUTH(start_latitude, start_longitude, finish_latitude, finish_longitude);
            Log.d(TAG, "두 지점의 좌표를 통해 얻어낸 방위각 : " + finish_azimuth);


            // 두 점 사이의 거리를 float 값으로 리턴 받는다.
            float current_distance = getDistance(start_latitude, start_longitude, finish_latitude, finish_longitude);
            String strNumber = String.format("%.1f", current_distance / 1000); // m 단위의 결과값을 km로 변경해주고, 소수점 첫째 자리까지만 표시
            tv_LeftDistance.setText("남은 거리: " + strNumber + " km"); // 목적지까지 남은 거리를 km 로 표시

            // progressDialog.dismiss();
            // progressOFF();
        }
    }

    // 위/경도를 이용해서 두 점 사이의 거리 측정
    public float getDistance(double lat1 , double lng1 , double lat2 , double lng2 ){
        float distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }
    // =========================================================================================================


    // =========================================================================================================
    // 내 휴대폰의 방향 센서 값 가져오기
    // =========================================================================================================

    // 내 휴대폰의 방향 센서 값을 받아오기 위한 함수
    private void orientationSensorOn() {
        orientationSensor.init(1.0, 1.0, 1.0);
        orientationSensor.on(2); // 0: Normal, 1: UI, 2: GAME, 3: FATEST
    }



    // 방향센서에서 데이터를 직접 받을수도 있고, 가속도 센서와 자기장 센서가 주는 데이터를 계산해서 얻을 수도 있음.
    @Override
    public void orientation(Double AZIMUTH, Double PITCH, Double ROLL) {
//        ((TextView) findViewById(R.id.tv_phone)).setText("AZIMUTH(방위각) : " + String.valueOf(AZIMUTH)); // 도착지까지 걸리는 시간 표시

        Log.d("Azimuth",String.valueOf(AZIMUTH)); // Degree임

        // double to int
//        Double azimuth = AZIMUTH;
//        Integer mDegree = azimuth.intValue();

//        final_degree; // 최종적으로 폰이 가리키는 방향

//        double azimuth = AZIMUTH;
//        float qwe = (float) azimuth;

        // 결과 방위각 값: finish_azimuth
        // 내 폰 방위각: azimuth

        float phone_azimuth = AZIMUTH.intValue(); // 내 폰이 가리키는 방향
        float subtract_degree;

        // 내 폰의 방위각 값에서 목적지 방위값을 뺀 값이
        if(phone_azimuth<finish_azimuth) {

//            final_degree = phone_azimuth + subtract_degree; // 내 폰의 방위각 - (내폰의 방위각 - 목적지 방위 값)
//            subtract_degree = Math.abs(subtract_degree); // 음수를 양수로 만들어 주기

//            subtract_degree = phone_azimuth - finish_azimuth + 360; // 내 폰의 방위각 값 - 목적지 방위 값

            subtract_degree = finish_azimuth - phone_azimuth;

        } else {

//            subtract_degree = Math.abs(subtract_degree); // 음수를 양수로 만들어 주기
//            final_degree = phone_azimuth - subtract_degree; // 내 폰의 방위각 - (내폰의 방위각 - 목적지 방위 값)
            subtract_degree = phone_azimuth - finish_azimuth; // 내 폰의 방위각 값 - 목적지 방위 값

        }

        Log.d(TAG, "subtract_degree ; " + subtract_degree);

        // ex1) 내 폰의 방위각 10, 목적지 방위각 90일 때
        //      => subtract_degree = 내폰의 방위각 - 목적지 방위각 (-80) => (80) 절대값으로 변경
        //      => final_degree = 내폰의 방위각 - subtract_degree
        // ex2) 내 폰의 방위각 180, 목적지 방위각 90일 때 =>

//        float phone_azimuth = AZIMUTH.intValue(); // 내 폰이 가리키는 방향
//        float subtract_degree;
//
//        if(finish_azimuth>phone_azimuth) {
//            subtract_degree = finish_azimuth - phone_azimuth; // 목적지 방향각 - 나의 방향각
//            Log.d(TAG, "finish_azimuth>phone_azimuth");
//            Log.d(TAG, "subtract_degree : " + subtract_degree);
//
//        } else {
////            subtract_degree = phone_azimuth - finish_azimuth; // 나의 방향각 - 목적지 방향각
//            subtract_degree = finish_azimuth - phone_azimuth; // 목적지 방향각 - 나의 방향각
//            subtract_degree = Math.abs(subtract_degree); // 음수를 양수로 만들어 주기
//
//            Log.d(TAG, "finish_azimuth<phone_azimuth");
//            Log.d(TAG, "subtract_degree : " + subtract_degree);
//        }


        // 이미지뷰 이미지 회전 시키기
        final Matrix matrix = new Matrix();
        iv_phone.setScaleType(ImageView.ScaleType.MATRIX);   // required
        matrix.postRotate((float) subtract_degree, 100, 100);
        iv_phone.setImageMatrix(matrix);
        tv_phone.setText("나의 폰의 방위각: " + subtract_degree);


/*        countDownTimer = new CountDownTimer(1000, 99999999) {

            // 1초씩 지날 떄 마다 발생하는 함수
            @Override
            public void onTick(long l) {
                Log.d(TAG, "l 값은? " + l);

                iv_phone.setImageMatrix(matrix);

            }

            @Override
            public void onFinish() {
            }
        };



        countDownTimer.start();*/


//        ((ImageView) findViewById(R.id.iv_phone)).setImageBitmap(rotateImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_navigation_white_24dp), finish_azimuth-qwe));


    }
    // =========================================================================================================

//    private void delayLotation() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // runOnUiThread를 추가하고 그 안에 UI작업을 한다.
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // 이미지뷰 이미지 회전 시키기
//                        final Matrix matrix = new Matrix();
//                        iv_phone.setScaleType(ImageView.ScaleType.MATRIX);   //required
//                        matrix.postRotate((float) final_degree, 100, 100);
//                        iv_phone.setImageMatrix(matrix);
//                        tv_phone.setText("나의 폰의 방위각: " + final_degree);
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        }).start();
//    }



    // =========================================================================================================
    // 카메라 Preview
    // =========================================================================================================

    // Camera priview initialize
    private void init() {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);

        // surfaceView setting
        previewHolder = cameraPreview.getHolder();
        previewHolder.addCallback(this);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    // SurfaceView가 생성될 때 발생하는 함수.
    // Camera와 SurfaceHolder와 연결하고 카메라 Preview를 시작.
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated, 여기서 camera preview가 생성되야 하는데");

        try {
            if (mCamera == null) {
                mCamera.setPreviewDisplay(previewHolder);
                mCamera.startPreview();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // SurfaceView의 상태가 변경될 때 마다 발생하는 함수.
    // SurfaceView에 맞게 카메라 Preview도 재설정한 후 다시 시작하게 함.
    @Override
    public void surfaceChanged(SurfaceHolder holder, int i, int i1, int i2) {
        Log.d(TAG, "surfaceChanged");

        // View 가 존재하지 않을 때
        if (previewHolder.getSurface() == null) {
            return;
        }

        // 작업을 위해 잠시 멈춘다
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // 에러가 나더라도 무시한다.
        }

        // 카메라 설정을 다시 한다.
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        mCamera.setParameters(parameters);

        // View 를 재생성한다.
        try {
            mCamera.setPreviewDisplay(previewHolder);
            mCamera.startPreview();
        }

        catch (Exception e) {

        }
    }

    // SurfaceView 객체가 사라지게 되면 발생하는 함수로 카메라 리소스를 반환
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroyed");

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    // =========================================================================================================
    // 기타 함수들
    // =========================================================================================================

    // ImageView Rotate 시킬 때 사용.
    public Bitmap rotateImage(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();

        // 회전 각도 셋팅
        matrix.postRotate(degree);

        // 이미지와 Matrix를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }


    // 두 지점의 좌표를 통해서 방위각 계산 하기
    public float getAZIMUTH(double start_latitude, double start_longitude, double finish_latitude, double finish_longitude) {

        // 현재 위치: 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에 라디안 각도로 변환
        double Cur_Lat_radian = start_latitude * (3.141592 / 180);
        double Cur_Lon_radian = start_longitude * (3.141592 / 180);

        // 목표 위치: 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에 라디안 각도로 변환한다.
        double Dest_Lat_radian = finish_latitude * (3.141592 / 180);
        double Dest_Lon_radian = finish_longitude * (3.141592 / 180);

        // radian distance
        double radian_distance = 0;
        radian_distance = Math.acos(Math.sin(Cur_Lat_radian) * Math.sin(Dest_Lat_radian) + Math.cos(Cur_Lat_radian) * Math.cos(Dest_Lat_radian) * Math.cos(Cur_Lon_radian - Dest_Lon_radian));

        // 목적지 이동 방향을 구한다.(현재 좌표에서 다음 좌표로 이동하기 위해서는 방향을 설정해야 한다. 라디안값이다.
        double radian_bearing = Math.acos((Math.sin(Dest_Lat_radian) - Math.sin(Cur_Lat_radian) * Math.cos(radian_distance))
                / (Math.cos(Cur_Lat_radian) * Math.sin(radian_distance)));        // acos의 인수로 주어지는 x는 360분법의 각도가 아닌 radian(호도)값이다.

        double true_bearing = 0;
        if(Math.sin(Dest_Lon_radian - Cur_Lon_radian) < 0) {
            true_bearing = radian_bearing * (180 / 3.141592);
            true_bearing = 360 - true_bearing;
        } else {
            true_bearing = radian_bearing * (180 / 3.141592);
        }

        return (float) true_bearing;

    }

    // 시현용 커스텀 다이얼로그 띄우기
    private void startProgress() {

        progressON("Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);
    }


    // 목적지 도착 알림 다이얼로그
    private void arriveDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ARNavigation_Activity.this);
        alertDialog.setTitle("Alert Message");
        alertDialog.setMessage("Congraturation! Arrived destination...");
        alertDialog.setPositiveButton("okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // 화면 종료
                        finish();
                    }
                });
        alertDialog.show();
    }




/*        progressON("Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);*/

    // =========================================================================================================




    // =========================================================================================================
    // 생명 주기
    // =========================================================================================================

    protected void onResume() {
        super.onResume();

/*        if (mCamera != null) {
            init(); // Camera Preview 시작
        }*/

        sendRequest(); // 구글 서버로부터 위치 이동 정보값 받아오기
        location(); // 위치값 받아오기 재시작

        orientationSensorOn(); // 나의 폰 방향 센서 값 받아오기 시작

    }


    protected void onPause() {
        super.onPause();

        // Camera Preview 재시작
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        // locationManager 해제
        locationManager.removeUpdates(mLocationListener);

        // Orientation Sensor 해제
        orientationSensor.off();
    }


}

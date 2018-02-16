package seopftware.fundmytravel.function.streaming;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.function.retrofit.HttpService;

import static seopftware.fundmytravel.function.MyApp.SERVER_URL;
import static seopftware.fundmytravel.function.MyApp.USER_ID;
import static seopftware.fundmytravel.function.MyApp.USER_NAME;

/**
 * 방송 전 방송 제목 입력하는 Activity
 * @author 김인섭
 * @version 1.0.0
 * @since 2018-01-06 오후 12:03
 * @class comment
 *   이 액티비티는 방송 제목 입력 후 방송을 시작하는 버튼이 있는 곳 입니다.
 **/
public class BeforeStreaming_Activity extends AppCompatActivity {

    private static final String TAG = "all_" + BeforeStreaming_Activity.class;

    // 서버로 이미지 파일을 전송하기 위한 변수
    Uri photoUri; // 이미지 사진 uri
    File photoFile;

    // UI 관련 변수들
    Button btn_streamstart; // 방송 시작
    ImageView iv_roomimage; // 이미지 뷰
    EditText et_title, et_tag; // 방송 제목, 방송 태그
    TextView tv_location;
    ImageButton ibtn_location;
    LinearLayout linear_location, linearLayout; // 위치 정보 클릭 시, 다른 뷰들 앞으로 꺼내기

    // 위치 정보를 받아오기 위한 google location
    LocationManager locationManager;
    double longitude; // 경도
    double latitude;

    // 변수
    String room_id; // 내가 만든 방의 고유 ID. 이 값은 스트리머가 방을 생성할 때 Streaming_Activity로 넘겨준다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beforestreaming);

        Intent intent = getIntent();
        photoUri = intent.getParcelableExtra("photoUri");
        Log.d(TAG, "Uri 값 : " + photoUri);

        // Uri to File (서버에 이미지 파일 전송하기 위해 변경)
        photoFile = new File(getPath(photoUri));



        // location manager 선언
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // UI
        et_title = (EditText) findViewById(R.id.et_title); // 제목
        et_tag = (EditText) findViewById(R.id.et_tag); // 태그
        tv_location = (TextView) findViewById(R.id.tv_location); // 나의 위치 표시
        ibtn_location = (ImageButton) findViewById(R.id.ibtn_location); // 나의 위치 받아오기


        // 바탕에 깔릴 배경 선택
        iv_roomimage= (ImageView) findViewById(R.id.iv_roomimage);
        Glide.with(getApplicationContext())
                .load(photoUri)
                .bitmapTransform(new BlurTransformation(getApplicationContext(), 50, 2)) // Glide Blur 효과
                .into(iv_roomimage);


        linearLayout= (LinearLayout) findViewById(R.id.linearLayout);
        linearLayout.bringToFront();
        linear_location= (LinearLayout) findViewById(R.id.linear_location);
        linear_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tv_location.setText("Finding your location");
                Log.d(TAG, "위치 확인 중");
                // GPS 허용여부 묻기
                chkGpsService();


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
            }
        });






        // 스트리밍 시작 버튼
        btn_streamstart = (Button) findViewById(R.id.btn_streamstart);
        btn_streamstart.bringToFront();
        // Go Live 버튼 클릭시 방송 시작
        btn_streamstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Broadcasting Start! :)", Toast.LENGTH_LONG).show();

                btn_streamstart.setEnabled(false); // 버튼 클릭 무효화

                // DB에 정보 입력
                DBInsert_roominfo();

//                // 버튼 더블 클릭 방지
//                Handler handler = new Handler();
//                handler.postDelayed(new delay(), 1000); // 1초 지연
            }
        });
    }

    class delay implements Runnable {
        public void run() {
            btn_streamstart.setEnabled(true); // 버튼 클릭 무효화 해제
        }
    }
    private final LocationListener mLocationListener = new LocationListener() {

        // 위치값이 갱신되면 이벤트 발생
        // 값은 Location 형태로 리턴
        @Override
        public void onLocationChanged(Location location) {

            Log.d(TAG, "onLocationChanged, location: " + location);

            Toast.makeText(getApplicationContext(), "위치 정보 확인 완료", Toast.LENGTH_SHORT).show();

            // 위치정보를 통해 받아오는 값들
            double altitude = location.getAltitude(); // 고도
            String provider = location.getProvider(); // 위치 제공자 (GPS or Network)
            longitude = location.getLongitude(); // 경도
            latitude = location.getLatitude(); // 위도

            tv_location.setText("location confirmed");
            Log.d(TAG, "provider : " + provider);
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


    // 보내는 값: 방 고유 번호, 방 참가자 수(int), 방 이름, 방 태그, 스트리머 이름, 메인 사진, 방송 여부
    private void DBInsert_roominfo() {

        // 제목 입력안했을 때
        if(et_title.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Input your streaming title", Toast.LENGTH_SHORT).show();
            et_title.requestFocus();
            return;

        }

        else if(et_tag.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Input your streaming tag", Toast.LENGTH_SHORT).show();
            et_tag.requestFocus();
            return;
        }

        else if(!tv_location.getText().equals("location confirmed")) {
            Toast.makeText(getApplicationContext(), "Please confirm your location", Toast.LENGTH_SHORT).show();
            return;
        }

        // HTTP 통신하는 곳 _ 방 정보를 RDB에 저장하는 곳
        saveStreamingRoom();

    }

    // 서버로 전송하는 부분 (http 통신)
    private void saveStreamingRoom() {

        // 날짜 받아오기
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy"+"MM" + "dd" + "HH" + "mm" + "ss");
        String time = format.format(date);

        // 서버로 보낼 변수들
        // 1
        room_id = USER_ID + time; // room_id

        // 2
        int room_numpeople = 1; // 방 참여자수는 일단 1로 표시 (자기 자신)

        // 3
        String room_name_title = et_title.getText().toString(); // 방 참여자수는 일단 1로 표시 (자기 자신)

        // 4
        String room_name_tag = et_tag.getText().toString();

        // 5
        String room_name_streamer = USER_NAME;

        // 6
        String room_image_path = photoFile.getName();

        // 7
        String room_status = "LIVE";

        // 8
        String room_location = String.valueOf(latitude +"_"+longitude); // 위도/경도

        // 파일 전송을 위한 request body
        RequestBody filepart = RequestBody.create(MediaType.parse("image/*"), photoFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("uploaded_file", photoFile.getName(), filepart);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 서버와 통신 주고 받는 곳
        HttpService httpService = retrofit.create(HttpService.class);
        Call<ResponseBody> comment = httpService.upload_roominfo(file, room_id, room_numpeople, room_name_title,
                room_name_tag, room_name_streamer, room_image_path, room_status, room_location);
        comment.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {
                    Log.d(TAG, "방 정보 RDB 입력 성공");

                    // 위치 정보 그만 받아오기
                    locationManager.removeUpdates(mLocationListener);

                    Log.d(TAG, "방 정보 RDB 입력 완료");


                    // 서버에 방송 정보가 완료되면 방송 시작
                    // 방송 시작
                    Intent intent = new Intent(getApplicationContext(), Streaming_Acticity.class);
                    intent.putExtra("room_id", room_id);
                    startActivity(intent);
                    finish();
                }


                else {
                    Log.d(TAG, "방 정보 RDB 입력 실패");
                }





            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.d(TAG, "****************************************************************");
                Log.d(TAG, "errer message");
                Log.d(TAG, "t.getMessage().toString() : " +  t.getMessage().toString());
                Log.d(TAG, "****************************************************************");

            }
        });
    }


    // 구글맵 GPS 허용 여부
    public boolean chkGpsService() {

        // 만약 GPS 기능이 꺼져있으면 GPS 기능 활성화 화면으로 안내
        String gps = android.provider.Settings.Secure.getString(getContentResolver(),
                android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            final AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setIcon(R.drawable.ic_menu_send);
            gsDialog.setMessage("GPS 사용 동의 후 위치 서비스 사용이 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            }).setNegativeButton("설정안함", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            }).create();
//            AlertDialog alertDialog = gsDialog.create();
//            gsDialog.show();
            return true;

        } else {
            return false;
        }
    }

    // URI -> FIle path로 변경
    // 기존에 받아온 URI path는 작동하지 않았다.
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
}

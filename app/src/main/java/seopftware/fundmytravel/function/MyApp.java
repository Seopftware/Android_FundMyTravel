package seopftware.fundmytravel.function;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import seopftware.fundmytravel.dataset.Parsing;
import seopftware.fundmytravel.function.retrofit.HttpService;
import seopftware.fundmytravel.function.retrofit.RetrofitClient;

/**
 * Application Class
 * @author 김인섭
 * @version 1.0.0
 * @class comment
 * @since 2017-12-25 오전 11:27
 * 이 클래스는 Application 클래스로 언제 어디서나 변수/메소드를 불러들일 수 있습니다.
 * 간편한 변수/메소드 접근을 위해 만들었습니다.
 * ex) 로그인 액티비티에서 받아온 사용자 아이디와 닉네임, 프사 등 저장 => 어디서나 사용가능
 **/

public class MyApp extends Application {

    public static String TAG ="all_"+"MyApp";

    public static String USER_ID;
    public static String USER_NAME;
    public static String USER_PHOTO;
    public static String AUTO_LOGIN_STATUS = "auto_login_status";
    public static String AUTO_LOGIN_KEY = "auto_login_key";
    public static String AUTO_LOGIN_USERID = "auto_login_userid";
    public static String SERVER_URL = "http://52.79.138.20/";
    public static String SERVER_IP1 = "192.168.144.2";
    public static String SERVER_IP = "172.30.1.17";
    public static String BROADCAST_NETTY_MESSAGE = "seopftware.fundmytravel.chatmessage.SEND_BROAD_CAST";
    public static int NETTY_PORT = 8000;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    // Toast 메세지를 간편하게 띄우기 위한 함수
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Retrofit http 통신 과정 Logging 하기
    public static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        return builder.build();
    }

    // 시간을 가르쳐 주는 함수 (채팅 메세지 날짜 저장할 때 필요)
    // 서버단에서 작업해도 되는데, JSON으로 주고 받을 때, 주고 받는 데이터 관리를 편하게 하기 위해 날짜도 클라에서 처리해줌.
    // 추후, Sync 작업할 때도 날짜를 입맛대로 관리하기 위해 클라에서 관리
    public static String TimeCheck() {
        long now = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 E요일_kkmmss", Locale.KOREA);
        Log.d("시간이 이상함", String.valueOf(simpleDateFormat));
        String Show_Time = simpleDateFormat.format(new Date(now));

//        String[] time_split = Show_Time.split("_");
//        String Date = time_split[0];
//        String Time = time_split[1];
//        Log.d("시간 확인", "Date : " + Date + " Time : " + Time);

        return Show_Time;
    }

    // 보내는 값: User ID
    // 받는 값: User_Nickname, User_Profile
    // 받은 값은 전역변수에 등록시킨 다음 모든 액티비티에서 끌어다가 사용하기
    // (그럼 내 정보 불러오기 위해 매번 http 통신안해도됨)
    public static void getMyInfo() {
        // Http 통신하는 부분
        Retrofit retrofit = RetrofitClient.getClient();
        HttpService httpService = retrofit.create(HttpService.class);
        Call<Parsing> comment = httpService.get_userinfo(USER_ID); // 1.User Id 보내고 정보 받아옴
        comment.enqueue(new Callback<Parsing>() {
            @Override
            public void onResponse(Call<Parsing> call, Response<Parsing> response) {
                Parsing parsing = response.body();
                String user_key = parsing.getResult().get(0).getUserKey();
                USER_ID = user_key;

                // 유저의 이름
                String user_name = parsing.getResult().get(0).getUserName();
                USER_NAME=user_name;

                // 유저의 프로필 사진
                String user_profile = parsing.getResult().get(0).getUserPhoto();
                USER_PHOTO=user_profile;
            }

            @Override
            public void onFailure(Call<Parsing> call, Throwable t) {

            }
        });
    }

    /*
    *         comment.enqueue(new Callback<ResponseBody>() { // 비동기로 Request를 보내고 Response가 돌아올 때 콜백으로 앱에게 알림
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {
                    Log.d(TAG, "유저 정보 가져오기 성공");

                } else {
                    Log.d(TAG, "유저 정보 가져오기 실패");
                    // 상태에 따라 코드 작업 처리할 때 (404, 500 등등)
                    int statusCode=response.code();

                }

                Log.d(TAG, "response.body().toString() : " + response.body().toString());
                try {
                    Log.d(TAG, "response.body().string() : " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {

                    String message=response.body().string();

                    // 서버에서 가져온 데이터(JSON 형태임)를 분해하는 작업
                    JSONObject j = new JSONObject(message);
//                    JSONArray jsonArray = jsonObject.getJSONArray("result");
//                    JSONObject jo = jsonArray.getJSONObject(0);

                    // 유저 고유 ID 번호
                    String user_key = jo.getString("user_key");
                    USER_ID = user_key;

                    // 유저의 이름
                    String user_name = jo.getString("user_name");
                    USER_NAME=user_name;

                    // 유저의 프로필 사진
                    String user_profile = jo.getString("user_photo");
                    USER_PHOTO=user_profile;

                    Log.d(TAG, "내 정보 변수에 저장 완료!!!");

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "네트워크 통신 실패");
                Log.d(TAG, "t.getMessage : " + t.getMessage());
            }
        }); // HTTP 통신 종료*/
}

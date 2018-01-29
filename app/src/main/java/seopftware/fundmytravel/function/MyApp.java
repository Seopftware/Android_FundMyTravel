package seopftware.fundmytravel.function;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    // 로그인한 유저를 전역에 사용할 변수
    public static int USER_ID; // 로그인한 유저의 고유 ID 번호
    public static String USER_NAME; // 로그인한 유저의 이름
    public static String USER_PHOTO; // 로그인한 유저의 프로필 사진 주소
    public static String USER_STATUS_MESSAGE; // 로그인한 유저의 상태 메세지

    // Shared Preference 키값
    public static String AUTO_LOGIN_STATUS = "auto_login_status"; // 유저가 자동로그인을 한 상태 인지 아닌지 확인
    public static String AUTO_LOGIN_KEY = "auto_login_key"; // 유저가 자동로그인을 한 상태 인지 아닌지 확인 (Value: Success / Fail)
    public static String AUTO_LOGIN_USERID = "auto_login_userid"; // 로그인한 유저의 ID를 꺼낼 때 사용 (Value: User ID 값 -> DB에 key값을 보낸 후 유저 정보 받을 때 사용)


    // Server 관련 정보들
    public static String SERVER_URL = "http://52.79.138.20/"; // AWS Server
    public static String SERVER_IP = "172.30.1.38"; // Netty Chat Server IP
    public static int NETTY_PORT = 8000; // Netty Chat Server Port

    // 이미지 파일 갯수 관리
    public static ArrayList<String> numberofpic = new ArrayList<>(); // 받은 이미지 파일 담아두는 곳
    public static int PIC_MESSAGE= 0; // 노티피케이션 메세지 알람 숫자


    // Broad cast Receiver_ intent filter 값들
    public static String BROADCAST_NETTY_MESSAGE = "seopftware.fundmytravel.chatmessage.SEND_BROAD_CAST"; // Netty_채팅 메세지
    public static String BROADCAST_NETTY_MESSAGE_PIC = "seopftware.fundmytravel.chatmessage.MESSAGE_PIC"; // Netty_영상 통화 거절시
    public static String BROADCAST_NETTY_VIDEOCALL = "seopftware.fundmytravel.chatmessage.SEND_VIDEO_CALL"; // Netty_영상 통화 걸 때
    public static String BROADCAST_NETTY_VIDEOCALL_DENY = "seopftware.fundmytravel.chatmessage.SEND_VIDEO_CALLDENY"; // Netty_영상 통화 거절시


    @Override
    public void onCreate() {
        super.onCreate();

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

/*      나중에 영상 보면서 채팅 보기 위해 싱크 할 때 사용할 예정
        String[] time_split = Show_Time.split("_");
        String Date = time_split[0];
        String Time = time_split[1];
        Log.d("시간 확인", "Date : " + Date + " Time : " + Time);*/


/*        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy" + "_" + "MM" + "_" + "dd" + "_" + "HH" + "_" + "mm" + "_" + "ss");
        String bmpName = format.format(date);

        File f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + bmpName + ".jpg");
        if (f.exists()) {
            f.delete();
        }*/

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
                int user_key = parsing.getResult().get(0).getUserKey();
                USER_ID = user_key;

                // 유저의 이름
                String user_name = parsing.getResult().get(0).getUserName();
                USER_NAME=user_name;

                // 유저의 프로필 사진
                String user_profile = parsing.getResult().get(0).getUserPhoto();
                USER_PHOTO=user_profile;

                // 유저의 상태 메세지
                String user_status = parsing.getResult().get(0).getUserStatus();
                USER_STATUS_MESSAGE=user_status;
            }

            @Override
            public void onFailure(Call<Parsing> call, Throwable t) {

            }
        });
    }

    // Toast 메세지를 간편하게 띄우기 위한 함수
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

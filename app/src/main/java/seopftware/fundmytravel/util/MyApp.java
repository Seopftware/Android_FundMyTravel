package seopftware.fundmytravel.util;

import android.app.Application;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

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

    public static String USER_ID;
    public static String AUTO_LOGIN_STATUS = "auto_login_status";
    public static String AUTO_LOGIN_KEY = "auto_login_key";
    public static String AUTO_LOGIN_USERID = "auto_login_userid";
    public static String SERVER_URL = "http://52.79.138.20/";

    @Override
    public void onCreate() {
        super.onCreate();

    }

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
}

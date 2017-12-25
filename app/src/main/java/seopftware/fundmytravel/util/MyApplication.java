package seopftware.fundmytravel.util;

import android.app.Application;
import android.widget.Toast;

/**
 * Application Class
 * @author 김인섭
 * @version 1.0.0
 * @since 2017-12-25 오전 11:27
 * @class comment
 *   이 클래스는 Application 클래스로 언제 어디서나 변수/메소드를 불러들일 수 있습니다.
 *   간편한 변수/메소드 접근을 위해 만들었습니다.
 *   ex) 로그인 액티비티에서 받아온 사용자 아이디와 닉네임, 프사 등 저장 => 어디서나 사용가능
**/

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

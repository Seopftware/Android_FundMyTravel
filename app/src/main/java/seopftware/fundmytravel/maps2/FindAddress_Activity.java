package seopftware.fundmytravel.maps2;

import android.content.Intent;
import android.os.Bundle;
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

import seopftware.fundmytravel.R;

public class FindAddress_Activity extends AppCompatActivity {

    private static final String TAG = "all_" + FindAddress_Activity.class;
    private WebView browser;
    private EditText et_Address;
    String address;


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

        // UI 설정
        et_Address = (EditText) findViewById(R.id.et_Address);

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
                            et_Address.setText(data);
                            address = et_Address.getText().toString();


                        }
                    });
                }
            }).start();
        }
    }


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
               if(et_Address.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "목적지를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    et_Address.requestFocus();
                    return true;
                }

                Log.d(TAG, "action button 클릭 후 address : " + address);

                // AR 길찾기 화면으로 목적지 주소 넘겨주기
                Intent intent=new Intent(getApplicationContext(), ARNavigation_Activity.class);
                intent.putExtra("address", address);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}

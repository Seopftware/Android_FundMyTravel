package seopftware.fundmytravel.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.function.streaming.ActivityLink;
import seopftware.fundmytravel.function.streaming.Streaming_Acticity;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static seopftware.fundmytravel.function.MyApp.SERVER_URL;
import static seopftware.fundmytravel.function.MyApp.USER_ID;
import static seopftware.fundmytravel.function.MyApp.USER_NAME;
import static seopftware.fundmytravel.function.MyApp.USER_PHOTO;

// 설정화면을 표시해주는 Fragament

@SuppressLint("ValidFragment")
public class Settings_Fragment extends Fragment {

    private static final String TAG = "Settings_Fragment";
    private List<ActivityLink> activities;

    // UI 변수 선언
    ImageView iv_ProfileBack, iv_ProfileFront, iv_SNS, iv_camera; // 프로필 사진 백그라운드, 프로필 사진, 연동된 SNS 여부, 카메라 이미지
    TextView tv_Name, tv_PhoneNum, tv_TotalStar, tv_SNSName; // 이름, 폰 번호, 코인 갯수, SNS 연동 여부

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, null);
        createList();

        // UI 선언
        iv_ProfileBack = (ImageView) v.findViewById(R.id.iv_ProfileBack); // 프로필 백그라운드 사진
        iv_ProfileFront = (ImageView) v.findViewById(R.id.iv_ProfileFront); // 프로필 사진

        iv_SNS = (ImageView) v.findViewById(R.id.iv_SNS); // SNS 여부 사진
        iv_camera = (ImageView) v.findViewById(R.id.iv_camera);
        iv_camera.bringToFront();

        tv_Name = (TextView) v.findViewById(R.id.tv_Name); // 이름
        tv_PhoneNum = (TextView) v.findViewById(R.id.tv_PhoneNum); // 폰 번호
        tv_TotalStar = (TextView) v.findViewById(R.id.tv_TotalStar); // 코인 갯수
        tv_SNSName = (TextView) v.findViewById(R.id.tv_SNSName); // SNS 허용 여부


        // 백그라운드 프로필 사진
        Glide.with(getContext())
                .load(SERVER_URL + "photo/"+USER_PHOTO)
                .bitmapTransform(new BlurTransformation(getContext(), 50, 2)) // Glide Blur 효과
                .into(iv_ProfileBack); // Blur 프로필

        // 원형 프로필 사진
        Glide.with(getContext())
                .load(SERVER_URL + "photo/"+USER_PHOTO)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(iv_ProfileFront); // 원형 프로필

        tv_Name.setText(USER_NAME);

        iv_SNS.bringToFront();
        iv_ProfileFront.bringToFront();


        messageStatus_Update();

        return v;
    }

    public static Settings_Fragment getInstance() {
        Settings_Fragment home_fragment = new Settings_Fragment();
        return home_fragment;
    }

    private void createList() {
        activities = new ArrayList<>();
        activities.add(new ActivityLink(new Intent(getContext(), Streaming_Acticity.class), "Streaming", JELLY_BEAN));
    }

    // HTTP 통신
    // 메세지를 읽은 상태 업데이트
    private void messageStatus_Update() {
        String url = "http://52.79.138.20/php/select/user_now.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "결과값 : " + response.toString());

                Log.d("parsing1", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    JSONObject jo = jsonArray.getJSONObject(0);

                    // 로그인 방법
                    String  user_method= jo.getString("user_method");
                    if(user_method.equals("gmail")) {

                        iv_SNS.setImageDrawable(getResources().getDrawable(R.drawable.google_icon));
                        tv_SNSName.setText("Google");
//                        iv_SNS.setImageResource(R.drawable.google_icon);

                    }

                    else if(user_method.equals("naver")) {
                        tv_SNSName.setText("Naver");
                        iv_SNS.setImageDrawable(getResources().getDrawable(R.drawable.naver_icon));

                    }


                    // 폰 번호
                    String user_phone = jo.getString("user_phone");
                    tv_PhoneNum.setText(user_phone);


                    // 현재 별풍선 갯수
                    String user_star = jo.getString("user_star");
                    tv_TotalStar.setText(user_star);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new Hashtable<>();
                map.put("user_key", String.valueOf(USER_ID));

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}
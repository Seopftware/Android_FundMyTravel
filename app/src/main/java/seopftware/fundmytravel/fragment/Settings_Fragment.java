package seopftware.fundmytravel.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.function.streaming.ActivityLink;
import seopftware.fundmytravel.function.streaming.Streaming_Acticity;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static seopftware.fundmytravel.function.MyApp.SERVER_URL;
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
        iv_ProfileBack.bringToFront();

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

}
package seopftware.fundmytravel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Random;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.fragment.Chatlist_Fragment;
import seopftware.fundmytravel.fragment.Home_Fragment;
import seopftware.fundmytravel.fragment.Settings_Fragment;
import seopftware.fundmytravel.fragment.Streaminglist_Fragment;
import seopftware.fundmytravel.function.ViewFindUtils;
import seopftware.fundmytravel.function.etc.TabEntity;
import seopftware.fundmytravel.function.googlemap.CustomMarkerClusteringDemoActivity;

import static seopftware.fundmytravel.function.MyApp.AUTO_LOGIN_STATUS;
import static seopftware.fundmytravel.function.MyApp.AUTO_LOGIN_USERID;
import static seopftware.fundmytravel.function.MyApp.USER_ID;


/**
 * 메인 화면의 액티비티
 * @author 김인섭
 * @version 1.0.0
 * @class comment
 * @since 2018-01-06 오전 11:23
 * 이 액티비티는 앱의 메인 화면을 나타내기 위한 용도로 만들어졌습니다.
 **/

public class Home_Activity extends AppCompatActivity {

    private static final String TAG = "all_"+"Home_Activity";

    // TabLayout with Icon
    Context mContext = this;
    ActionBar actionBar;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles = {"Home", "Messages", "Streaming", "Settings"}; // 홈(친구 목록), 메세지(메세지 창), 방송, 설정

    // Tab 선택 안됐을 때의 아이콘들
    private int[] mIconUnselectIds = {
            R.mipmap.tab_home_unselect, R.mipmap.tab_speech_unselect,
            R.mipmap.tab_contact_unselect, R.mipmap.tab_more_unselect};

    // Tab 선택 됐을 때의 아이콘들
    private int[] mIconSelectIds = {
            R.mipmap.tab_home_select, R.mipmap.tab_speech_select,
            R.mipmap.tab_contact_select, R.mipmap.tab_more_select};


    //
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>(); // TabLayout 커스텀한 객체가 담겨 있는 ArrayList
    private View mDecorView;
    private ViewPager mViewPager; // ViewPager 변수
    private CommonTabLayout mTabLayout_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // setSupportActionBar 는 현재 액션바가 없으니 툴바를 액션바로 대체 하겠다는 뜻이고,
        // actionBar 객체를 생성한 이유는 액션바를 커스터마이징 하기 위한 것입니다.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SharedPreferences autologin = getSharedPreferences(AUTO_LOGIN_STATUS, Activity.MODE_PRIVATE);
        int user_id = autologin.getInt(AUTO_LOGIN_USERID, 0);
        USER_ID = user_id;

            // 로그인 한 경험이 있는 회원은 자동으로 Home 화면으로 넘어가게끔.
            USER_ID = autologin.getInt(AUTO_LOGIN_USERID, 0); // USER_ID 전역 변수에 고유 ID 값 담아서 어디서든 사용하기 편하게 해준다.
            Log.d(TAG, "USER_ID 값은? : " + USER_ID);

        Log.d(TAG, "USER ID값 궁금해: " + USER_ID);
        Log.d(TAG, "USER_ID 값을 서버로 보내다. (USER_ID) : " + USER_ID);


        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        // View Pager add
        mFragments.add(Home_Fragment.getInstance());
        mFragments.add(Chatlist_Fragment.getInstance());
        mFragments.add(Streaminglist_Fragment.getInstance());
        mFragments.add(Settings_Fragment.getInstance());

        // TabLayout 추가
        for (int i = 0; i < mTitles.length; i++) {

            Log.d(TAG, "mTitles.length : " + mTitles.length);
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i])); // 타이틀, 선택된 아이콘, 선택되지 않은 아이콘

        }


        mDecorView = getWindow().getDecorView();
        mViewPager = ViewFindUtils.find(mDecorView, R.id.vp_2); // 뷰페이저 (tab layout 하단의 view-fragment 장착)
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        mTabLayout_2 = ViewFindUtils.find(mDecorView, R.id.tl_2); // 탭 레이아웃 (아이콘 선택하는 view)
        tl_2();

        // 메세지 갯수 표시_메세지 받으면 카톡 처럼 안 읽은 메세지 갯수 표시
/*        mTabLayout_2.showMsg(0, 55);
        mTabLayout_2.setMsgMargin(0, -5, 5);

        mTabLayout_2.showMsg(1, 100);
        mTabLayout_2.setMsgMargin(1, -5, 5);


         점으로 표시
        mTabLayout_2.showDot(2);
        MsgView rtv_2_2 = mTabLayout_2.getMsgView(2);
        if (rtv_2_2 != null) {
            UnreadMsgUtils.setSize(rtv_2_2, dp2px(7.5f));
        }

         점에 색깔 주는 것도 가능
        mTabLayout_2.showMsg(3, 5);
        mTabLayout_2.setMsgMargin(3, 0, 5);
        MsgView rtv_2_3 = mTabLayout_2.getMsgView(3);
        if (rtv_2_3 != null) {
            rtv_2_3.setBackgroundColor(Color.parseColor("#6D8FB0"));
        }*/


    }


    Random mRandom = new Random();

    private void tl_2() {
        mTabLayout_2.setTabData(mTabEntities);
        mTabLayout_2.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            // 탭 2번 클릭시 발생
            @Override
            public void onTabReselect(int position) {

                // 안 읽은 메세지 갯수 표시
                /*                if (position == 0) {
                    mTabLayout_2.showMsg(0, mRandom.nextInt(100) + 1);
                    UnreadMsgUtils.show(mTabLayout_2.getMsgView(0), mRandom.nextInt(100) + 1);
                }*/
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                // Actionbar 타이틀 설정하는 곳
                if(position==0) {
                    // 액션바에 Friends (친구숫자) 표현하기
                    actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "friends " + "</font>"+"<font color=\"#FFC60B\">" + "350" + "</font>"));
                }

                else if(position==1) {
                    actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "message" + "</font>"));
                }

                else if(position==2) {
                    actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "streaming" + "</font>"));
                }

                else if(position==3) {
                    actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "settings" + "</font>"));
                }

            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout_2.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setCurrentItem(0);
    }

    // Viewpager Adapter
    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }


    // =========================================================================================================
    // Menu 부분 다루는 함수들

    // Menu inflate 작업
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_activity, menu);
        return true;
    }


    //액션바 백키 버튼 구현
    // 메뉴에 해당하는 아이템들 클릭 시 호출되는 함수
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.navidrawer: {
                Toast.makeText(getApplicationContext(), "maps open", Toast.LENGTH_SHORT).show();

//                Intent intent=new Intent(getApplicationContext(), GoogleMap_Main_Activity.class);
                Intent intent=new Intent(getApplicationContext(), CustomMarkerClusteringDemoActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.navidrawer2: {
                Toast.makeText(getApplicationContext(), "search open", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // =========================================================================================================

}


/*    protected int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }*/
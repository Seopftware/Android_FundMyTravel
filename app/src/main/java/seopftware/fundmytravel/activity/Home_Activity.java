package seopftware.fundmytravel.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.utils.UnreadMsgUtils;

import java.util.ArrayList;
import java.util.Random;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.fragment.Chatlist_Fragment;
import seopftware.fundmytravel.fragment.Home_Fragment;
import seopftware.fundmytravel.fragment.Settings_Fragment;
import seopftware.fundmytravel.fragment.Streaminglist_Fragment;
import seopftware.fundmytravel.function.ViewFindUtils;
import seopftware.fundmytravel.function.tablayout.TabEntity;

import static seopftware.fundmytravel.function.MyApp.BROADCAST_NETTY_VIDEOCALL;
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
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles = {"Home", "Messages", "Streaming", "Settings"};

    // 선택 안됐을 때의 아이콘들
    private int[] mIconUnselectIds = {
            R.mipmap.tab_home_unselect, R.mipmap.tab_speech_unselect,
            R.mipmap.tab_contact_unselect, R.mipmap.tab_more_unselect};

    // 선택 됐을 때의 아이콘들
    private int[] mIconSelectIds = {
            R.mipmap.tab_home_select, R.mipmap.tab_speech_select,
            R.mipmap.tab_contact_select, R.mipmap.tab_more_select};


    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>(); // TabLayout 커스텀한 객체가 담겨 있는 ArrayList
    private View mDecorView;
    private ViewPager mViewPager; // ViewPager 변수
    private CommonTabLayout mTabLayout_2;

    // 브로드 캐스트 리시버 동적 생성(매니페스트 intent filter 추가 안하고)
    BroadcastReceiver broadcast_receiver; // 서비스로부터 메세지를 받기 위해 브로드 캐스트 리시버 동적 생성
    IntentFilter intentfilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // setSupportActionBar 는 현재 액션바가 없으니 툴바를 액션바로 대체 하겠다는 뜻이고,
        // actionBar 객체를 생성한 이유는 액션바를 커스터마이징 하기 위한 것입니다.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "USER ID값 궁금해: " + USER_ID);
        Log.d(TAG, "USER_ID 값을 서버로 보내다. (USER_ID) : " + USER_ID);



        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        for (String title : mTitles) {
            mFragments.add(Home_Fragment.getInstance("Switch ViewPager " + title));
            mFragments.add(Chatlist_Fragment.getInstance("Switch ViewPager " + title));
            mFragments.add(Streaminglist_Fragment.getInstance("Switch ViewPager " + title));
            mFragments.add(Settings_Fragment.getInstance("Switch ViewPager " + title));
        }

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }

        mDecorView = getWindow().getDecorView();
        mViewPager = ViewFindUtils.find(mDecorView, R.id.vp_2);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        /** with ViewPager */
        mTabLayout_2 = ViewFindUtils.find(mDecorView, R.id.tl_2);
        tl_2();

        // 메세지 갯수 표시
//        mTabLayout_2.showMsg(0, 55);
//        mTabLayout_2.setMsgMargin(0, -5, 5);

//        mTabLayout_2.showMsg(1, 100);
//        mTabLayout_2.setMsgMargin(1, -5, 5);


        // 점으로 표시
//        mTabLayout_2.showDot(2);
//        MsgView rtv_2_2 = mTabLayout_2.getMsgView(2);
//        if (rtv_2_2 != null) {
//            UnreadMsgUtils.setSize(rtv_2_2, dp2px(7.5f));
//        }

        // 점에 색깔 주는 것도 가능
//        mTabLayout_2.showMsg(3, 5);
//        mTabLayout_2.setMsgMargin(3, 0, 5);
//        MsgView rtv_2_3 = mTabLayout_2.getMsgView(3);
//        if (rtv_2_3 != null) {
//            rtv_2_3.setBackgroundColor(Color.parseColor("#6D8FB0"));
//        }


        // 브로드 캐스트 관련
        intentfilter = new IntentFilter(); // 인텐트 필터 생성
        intentfilter.addAction(BROADCAST_NETTY_VIDEOCALL); // 인텐트 필터에 액션 추가
        register_receiver(); // 리시버 등록하는 함수 작동

    }


    // 영상 통화 브로드 캐스트 메세지 받아오는 곳
    // 여기서 Home_Activity -> Videocall_Receive_Activity 이동하는 작업 진행
    private void register_receiver() {
        broadcast_receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String user_id = intent.getStringExtra("user_id"); // 전화건 사람
                String receiver_id = intent.getStringExtra("receiver_id"); // 전화 받는 사람
                String room_number = intent.getStringExtra("room_number"); // webRTC 방번호

                Log.d(TAG, "user_id (서버에서 받은 메세지 (from Service) : " + user_id);
                Log.d(TAG, "receiver_id (서버에서 받은 메세지 (from Service) : " + receiver_id);
                Log.d(TAG, "room_number (서버에서 받은 메세지 (from Service) : " + room_number);

                Log.d(TAG, "****************************************************************");
                Log.d(TAG, "BroadcastReceiver() : (받기) 2.서비스에서 받은 메세지를 리스트뷰에 추가하는 곳");
                Log.d(TAG, "****************************************************************");


                // 보내는 값: caller_id, room_number
                // 보내는 곳: Videocall_Receive_Activity
                Intent callintent=new Intent(getApplicationContext(), Videocall_Receive_Activity.class);
                callintent.putExtra("user_id", user_id); // 전화를 건 사람
                callintent.putExtra("receiver_id", receiver_id); // 전화를 받는 사람
                callintent.putExtra("room_number", room_number);
                startActivity(callintent);
            }
        };

        registerReceiver(broadcast_receiver, intentfilter);
        Log.d(TAG, "video call broadcast receiver를 시작합니다.");

    }

    
    
    
    
    
    
    

    Random mRandom = new Random();

    private void tl_2() {
        mTabLayout_2.setTabData(mTabEntities);
        mTabLayout_2.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (position == 0) {
                    mTabLayout_2.showMsg(0, mRandom.nextInt(100) + 1);
                    UnreadMsgUtils.show(mTabLayout_2.getMsgView(0), mRandom.nextInt(100) + 1);
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

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

    protected int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    // 뷰로 inflate 시켜주기
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
                Toast.makeText(getApplicationContext(), "open", Toast.LENGTH_SHORT).show();
            }

            case R.id.navidrawer2: {
                Toast.makeText(getApplicationContext(), "open", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 동적으로(코드상으로) 브로드 캐스트 종료
    private void unregister_receiver() {
        if(broadcast_receiver !=null) {
            this.unregisterReceiver(broadcast_receiver);
            broadcast_receiver=null;
            Log.d(TAG, "broadcast receiver를 종료합니다.");
        }
    }
    // =========================================================================================================

    @Override
    public void onStop() {
        super.onStop();

        unregister_receiver();
    }

}

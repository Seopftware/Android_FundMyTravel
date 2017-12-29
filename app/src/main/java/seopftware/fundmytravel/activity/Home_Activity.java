package seopftware.fundmytravel.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import seopftware.fundmytravel.fragment.Streaminglist_Fragment;
import seopftware.fundmytravel.util.ViewFindUtils;
import seopftware.fundmytravel.util.tablayout.TabEntity;

public class Home_Activity extends AppCompatActivity {

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

    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private View mDecorView;
    private ViewPager mViewPager;
    private CommonTabLayout mTabLayout_2;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // setSupportActionBar 는 현재 액션바가 없으니 툴바를 액션바로 대체 하겠다는 뜻이고,
        // actionBar 객체를 생성한 이유는 액션바를 커스터마이징 하기 위한 것입니다.

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        for (String title : mTitles) {
            mFragments.add(Home_Fragment.getInstance("Switch ViewPager " + title));
            mFragments.add(Chatlist_Fragment.getInstance("Switch ViewPager " + title));
            mFragments.add(Streaminglist_Fragment.getInstance("Switch ViewPager " + title));
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

        mViewPager.setCurrentItem(1);
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
}

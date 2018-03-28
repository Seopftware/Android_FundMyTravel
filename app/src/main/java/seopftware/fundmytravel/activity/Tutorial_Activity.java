package seopftware.fundmytravel.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.fragment.ProductTourFragment;

public class Tutorial_Activity extends AppCompatActivity {

    private static final String TAG = "Tutorial_Activity";

    private static int NUM_PAGES = 6; // 튜토리얼 화면의 갯수

    // ViewPager
    ViewPager pager;
    PagerAdapter pagerAdapter;

    // UI 관련 변수
    LinearLayout linear_circles; // 넘기기 버튼에 표시되는 페이지를 원의 갯수로 나타냄
    Button btn_skip; // skip 버튼
    Button btn_done; // done 버튼
    ImageButton ibtn_next; // next 버튼
    boolean isOpaque = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 최상단의 윈도우바 없애기
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_tutorial);

        // UI 선언
        btn_skip = (Button) findViewById(R.id.btn_skip); // skip 버튼
        btn_done = (Button) findViewById(R.id.btn_done); // done 버튼
        ibtn_next = (ImageButton) findViewById(R.id.ibtn_next); // next 이미지 버튼

        // 클릭 이벤트
        // Tutorlai 생략하기
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 튜토리얼 소개의 끝 화면으로 가기 전까지는 SKIP 버튼이 생성
                // 튜토리얼 소개의 마지막 화면은 DONE 버튼이 생성
                // Skip 버튼 클릭 시
                // 자동 로그인이 설정되어 있으면 바로 앱내 화면으로 넘어가고
                // 만약, 자동 로그인이 설정되어 있지 않으면 로그인 화면으로
                endTutorial();
            }
        });

        // Tutorial 마치기
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTutorial();
            }
        });

        // Tutorial 다음 화면으로 넘어가기 버튼
        ibtn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // viewPager 다음화면으로 넘어가기
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            }
        });

        // ViewPager 적용
        pager = (ViewPager) findViewById(R.id.pager); // viewPager
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new CrossfadePageTransformer());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == NUM_PAGES -2 && positionOffset >0 ) {
                    if(isOpaque) {
                        pager.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        pager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == NUM_PAGES - 2) {
                    btn_skip.setVisibility(View.GONE);
                    ibtn_next.setVisibility(View.GONE);
                    btn_done.setVisibility(View.VISIBLE);
                } else if (position < NUM_PAGES - 2) {
                    btn_skip.setVisibility(View.VISIBLE);
                    ibtn_next.setVisibility(View.VISIBLE);
                    btn_done.setVisibility(View.GONE);
                }
                else if (position == NUM_PAGES - 1) {
                    endTutorial();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buildCircles();


    }

    private void buildCircles() {
        linear_circles = LinearLayout.class.cast(findViewById(R.id.linear_circles));

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for(int i=0; i<NUM_PAGES-1; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.ic_swipe_indicator_white_18dp);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            linear_circles.addView(circle);
        }

        setIndicator(0);
    }


    private void setIndicator(int index) {
        if(index < NUM_PAGES) {
            for (int i=0; i < NUM_PAGES -1; i++) {
                ImageView circle = (ImageView) linear_circles.getChildAt(i);

                if(i==index) {
                    circle.setColorFilter(getResources().getColor(R.color.text_selected));
                } else {
                    circle.setColorFilter(getResources().getColor(android.R.color.transparent));
                }
            }
        }
    }

    // Tutorlai Skip 버튼 클릭 시 발생하는 함수
    private void endTutorial(){
        Intent intent=new Intent(getApplicationContext(), Login_Activity.class);
//        Intent intent=new Intent(getApplicationContext(), StreamerFinish_Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    // PageView Adapter
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ProductTourFragment tp = null;
            switch(position){
                case 0:
                    tp = ProductTourFragment.newInstance(R.layout.welcome_fragment1);
                    break;
                case 1:
                    tp = ProductTourFragment.newInstance(R.layout.welcome_fragment2);
                    break;
                case 2:
                    tp = ProductTourFragment.newInstance(R.layout.welcome_fragment3);
                    break;
                case 3:
                    tp = ProductTourFragment.newInstance(R.layout.welcome_fragment4);
                    break;
                case 4:
                    tp = ProductTourFragment.newInstance(R.layout.welcome_fragment5);
                    break;
                case 5:
                    tp = ProductTourFragment.newInstance(R.layout.welcome_fragment6);
                    break;
            }

            return tp;
        }

        // Viewpage Fragment 갯수
        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            // Fragment 뷰 선언
            View backgroundView = page.findViewById(R.id.welcome_fragment);
            View text_head= page.findViewById(R.id.heading);
            View text_content = page.findViewById(R.id.content);

            // Welcome Fragment1
            View object1 = page.findViewById(R.id.a000);


            if(0 <= position && position < 1){
                ViewHelper.setTranslationX(page,pageWidth * -position);
            }
            if(-1 < position && position < 0){
                ViewHelper.setTranslationX(page,pageWidth * -position);
            }

            if(position <= -1.0f || position >= 1.0f) {
            } else if( position == 0.0f ) {
            } else {
                if(backgroundView != null) {
                    ViewHelper.setAlpha(backgroundView,1.0f - Math.abs(position));

                }

                if (text_head != null) {
                    ViewHelper.setTranslationX(text_head,pageWidth * position);
                    ViewHelper.setAlpha(text_head,1.0f - Math.abs(position));
                }

                if (text_content != null) {
                    ViewHelper.setTranslationX(text_content,pageWidth * position);
                    ViewHelper.setAlpha(text_content,1.0f - Math.abs(position));
                }

                if (object1 != null) {
                    ViewHelper.setTranslationX(object1,pageWidth * position);
                }

            } // else finish
        } // transFormpage finish
    } // Crossfade PageTransfomer finish

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pager!=null){
            pager.clearOnPageChangeListeners();
        }
    }
} // Activity finish

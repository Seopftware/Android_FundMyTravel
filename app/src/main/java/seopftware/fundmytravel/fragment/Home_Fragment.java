package seopftware.fundmytravel.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.adapter.Home_Recycler_Adapter;
import seopftware.fundmytravel.dataset.Home_Recycler_Item;
import seopftware.fundmytravel.util.streaming.ActivityLink;
import seopftware.fundmytravel.util.streaming.Streaming_Acticity;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;

@SuppressLint("ValidFragment")
public class Home_Fragment extends Fragment {

    private String mTitle;
    private List<ActivityLink> activities;

    // Recycler View
    RecyclerView recyclerView;
    Home_Recycler_Adapter adapter;
    Home_Recycler_Item recycler_item;
    ArrayList<Home_Recycler_Item> recycler_itemlist;


    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static Home_Fragment getInstance(String title) {
        Home_Fragment home_fragment = new Home_Fragment();
        home_fragment.mTitle = title;
        return home_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 액션바에 Friends (친구숫자) 표현하기
        getActivity().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Friends " + "</font>"+"<font color=\"#FFC60B\">" + "350" + "</font>"));

        View v = inflater.inflate(R.layout.fragment_home, null);
        createList();

        if (!hasPermissions(getContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 1);
        }

        recyclerView = (RecyclerView) v.findViewById(R.id.home_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_itemlist = new ArrayList<Home_Recycler_Item>();

        recycler_item = new Home_Recycler_Item();
        adapter = new Home_Recycler_Adapter();

        recyclerView.setAdapter(adapter);


        adapter.addMe("나다", "재밌다", "1.jpg");
        adapter.addFriend("친구다", "재밌다", "1.jpg");
        adapter.addFriend("친구다", "재밌다","1.jpg");
        adapter.addFriend("친구다", "재밌다","1.jpg");
        adapter.addFriend("친구다", "재밌다","1.jpg");
        adapter.addFriend("친구다", "재밌다","1.jpg");
        adapter.addFriend("친구다", "재밌다","1.jpg");
        adapter.notifyDataSetChanged();

        return v;
    }

    private void createList() {
        activities = new ArrayList<>();
        activities.add(new ActivityLink(new Intent(getContext(), Streaming_Acticity.class), "Streaming", JELLY_BEAN));
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}

/**
 // =========================================================================================================
 // 권한 설정
 private void showMinSdkError(int minSdk) {
 String named;
 switch (minSdk) {
 case JELLY_BEAN_MR2:
 named = "JELLY_BEAN_MR2";
 break;
 case LOLLIPOP:
 named = "LOLLIPOP";
 break;
 default:
 named = "JELLY_BEAN";
 break;
 }
 Toast.makeText(getContext(), "You need min Android " + named + " (API " + minSdk + " )", Toast.LENGTH_SHORT).show();
 }

 private void showPermissionsErrorAndRequest() {
 Toast.makeText(getContext(), "You need permissions before", Toast.LENGTH_SHORT).show();
 ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 1);
 }
 // =========================================================================================================

 */
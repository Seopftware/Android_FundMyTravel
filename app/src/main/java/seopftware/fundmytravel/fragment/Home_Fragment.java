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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.activity.Home_Profile_Activity;
import seopftware.fundmytravel.adapter.Home_Recycler_Adapter;
import seopftware.fundmytravel.dataset.Home_Recycler_Item;
import seopftware.fundmytravel.dataset.Parsing;
import seopftware.fundmytravel.function.etc.RecyclerItemClickListener;
import seopftware.fundmytravel.function.retrofit.HttpService;
import seopftware.fundmytravel.function.retrofit.RetrofitClient;
import seopftware.fundmytravel.function.streaming.ActivityLink;
import seopftware.fundmytravel.function.streaming.Streaming_Acticity;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static seopftware.fundmytravel.function.MyApp.USER_ID;
import static seopftware.fundmytravel.function.MyApp.USER_NAME;
import static seopftware.fundmytravel.function.MyApp.USER_PHOTO;
import static seopftware.fundmytravel.function.MyApp.USER_STATUS_MESSAGE;

@SuppressLint("ValidFragment")
public class Home_Fragment extends Fragment {

    private static final String TAG = "all_"+Home_Fragment.class;

    private String mTitle;
    private List<ActivityLink> activities;

    // Recycler View
    RecyclerView recyclerView;
    Home_Recycler_Adapter adapter;
    Home_Recycler_Item recycler_item;
    ArrayList<Home_Recycler_Item> recycler_itemlist;


    // 허용 여부
    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static Home_Fragment getInstance() {
        Home_Fragment home_fragment = new Home_Fragment();
        return home_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
        adapter = new Home_Recycler_Adapter(recycler_itemlist);
        recyclerView.setAdapter(adapter);

        // Recycler View Click Listener
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                // 내 프로필을 클릭한 경우 (영통 버튼 없애기
                if(position==0) {

                    Toast.makeText(getContext(), "프로필은 수정은 설정화면에서...", Toast.LENGTH_LONG).show();

                } else {
                    int friendsid = recycler_itemlist.get(position).getHome_id();

                    Log.d(TAG, "recycler_itemlist.get(position).getHome_id() : " + recycler_itemlist.get(position).getHome_id());
                    Log.d(TAG, "recycler_itemlist.get(position).getHome_message() : " + recycler_itemlist.get(position).getHome_message());

                    Intent intent=new Intent(getContext(), Home_Profile_Activity.class);
                    intent.putExtra("FRIENDS_ID",friendsid);
                    startActivity(intent);
                    Log.d(TAG, "Connect_Activity로 넘어갑니다.");

                }



            }

            @Override
            public void onLongItemClick(View view, int position) {
                Toast.makeText(getContext(),position+"번 째 아이템 롱 클릭",Toast.LENGTH_SHORT).show();

            }
        }));


        getFriendslist();


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

    // 보내는 값: 나의 고유 ID
    // 받는 값: 친구 목록(친구 ID, Name, Photo, Status(상태메세지)
    private void getFriendslist() {
        // Http 통신하는 부분
        Retrofit retrofit = RetrofitClient.getClient();
        HttpService httpService = retrofit.create(HttpService.class);
        Call<Parsing> comment = httpService.get_friendslist(USER_ID); // 1.User Id 보내고 친구 정보 받아옴
        comment.enqueue(new Callback<Parsing>() {
            @Override
            public void onResponse(Call<Parsing> call, Response<Parsing> response) {
                Parsing parsing = response.body();

                adapter.addMe(USER_NAME, USER_STATUS_MESSAGE, USER_PHOTO); // NAME, 상태 메세지, PROFILE

                //Todo 여기 무한 반복 어떻게?
                // 친구목록을 뿌려주는 곳
                for (int i=0; i<parsing.getFriendCount(); i++) {

                    // 친구의 고유 ID
                    int friends_id = parsing.getFriendslist().get(i).getUserFriendsId();

                    // 친구의 이름
                    String friends_name = parsing.getFriendslist().get(i).getUserName();

                    // 친구의 프로필 사진
                    String friends_photo = parsing.getFriendslist().get(i).getUserPhoto();

                    // 친구의 상태메세지
                    String friends_stats = parsing.getFriendslist().get(i).getUserName();

                    // 친구
                    adapter.addFriend(friends_id, friends_name, friends_stats, friends_photo); // ID, NAME, MESSAGE, STATUS

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<Parsing> call, Throwable t) {

            }
        });
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
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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.function.streaming.ActivityLink;
import seopftware.fundmytravel.function.streaming.BeforeStreaming_Activity;
import seopftware.fundmytravel.function.streaming.PlayerStreaming_Activity;
import seopftware.fundmytravel.function.streaming.Streaming_Acticity;
import seopftware.fundmytravel.activity.Home_Profile_Activity;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static seopftware.fundmytravel.function.MyApp.USER_ID;
import static seopftware.fundmytravel.function.chatting.Chat_Service.channel;

@SuppressLint("ValidFragment")
public class Streaminglist_Fragment extends Fragment {
    private String mTitle;
    private List<ActivityLink> activities;

    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static Streaminglist_Fragment getInstance(String title) {
        Streaminglist_Fragment home_fragment = new Streaminglist_Fragment();
        home_fragment.mTitle = title;
        return home_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(Html.fromHtml("<font color=\"red\">" + getString(R.string.app_name) + "</font>"));
        getActivity().getResources().getColor(android.R.color.white);


        View v = inflater.inflate(R.layout.fragment_streaminglist, null);
        createList();

        if (!hasPermissions(getContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 1);
        }

        Button btn_viewer = (Button) v.findViewById(R.id.btn_viewer);
        btn_viewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), PlayerStreaming_Activity.class);
                startActivity(intent);
            }
        });


        Button btn_call = (Button) v.findViewById(R.id.btn_call);
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), Home_Profile_Activity.class);
                startActivity(intent);
            }
        });


        Button btn_server = (Button) v.findViewById(R.id.btn_server);
        btn_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), "서버로 메세지 보냄" + USER_ID, Toast.LENGTH_LONG).show();
                channel.writeAndFlush(USER_ID + "데이터가 서버에 옵니까???");

            }
        });

        Button btn_streamer = (Button) v.findViewById(R.id.btn_streamer);
        btn_streamer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPermissions(getContext(), PERMISSIONS)) {
                    ActivityLink link = activities.get(0);
                    int minSdk = link.getMinSdk();
                    if (Build.VERSION.SDK_INT >= minSdk) {
                        startActivity(link.getIntent());
                    } else {
                        showMinSdkError(minSdk);
                    }
                } else {
                    showPermissionsErrorAndRequest();
                }

                Intent intent=new Intent(getContext(), BeforeStreaming_Activity.class);
                startActivity(intent);
            }
        });



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

}
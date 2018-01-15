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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.function.streaming.ActivityLink;
import seopftware.fundmytravel.function.streaming.Streaming_Acticity;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;

@SuppressLint("ValidFragment")
public class Settings_Fragment extends Fragment {
    private List<ActivityLink> activities;

    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static Settings_Fragment getInstance() {
        Settings_Fragment home_fragment = new Settings_Fragment();
        return home_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, null);
        createList();

        if (!hasPermissions(getContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 1);
        }

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
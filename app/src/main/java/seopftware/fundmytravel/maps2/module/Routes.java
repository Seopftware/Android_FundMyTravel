package seopftware.fundmytravel.maps2.module;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by MSI on 2018-01-29.
 */

public class Routes {

    public Distance distance; // 도착지까지 총 거리
    public Duration duration; // 도착지까지 총 걸리는 시간

    public String endAddress; // 도착지 주소명
    public LatLng endLocation; // 도착지의 위/경도

    public String startAddress; // 출발지 주소명
    public LatLng startLocation; // 출발지의 위/경도

    public List<LatLng> points; // polyLine points


//    public HashMap<String, Routes> point = new HashMap<String, Routes>();
//    public HashMap<String, Distance> point;

}

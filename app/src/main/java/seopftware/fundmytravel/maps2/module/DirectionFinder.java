package seopftware.fundmytravel.maps2.module;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MSI on 2018-01-29.
 */

public class DirectionFinder {

    private static final String TAG = "all_" + DirectionFinder.class;
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyBj0cRD7olMBfepjnajXYJtsoQTbNjwCnM"; // Google MAP API가 아닌 Google Maps Directions API 키임.

    private DirectionFinderListener listener;
    private String origin; // 출발지 (현재 내 위치)
    private String destination; // 도착지

    // 생성자
    public DirectionFinder(DirectionFinderListener listener, String origin, String destination) {

        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
    }

    // execute() function will download raw json data from link first
    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());

    }

    // 서버로 보낼 URL 주소 값을 생성하는 곳
    private String createUrl() throws UnsupportedEncodingException {
        // 아스키 문자 ㅡ> 특수한 문자로 변환 (url 주소에서 사용하기 위해)
        String urlOrigin = URLEncoder.encode(origin, "UTF-8"); // 인코딩된 출발지명
        String urlDestination = URLEncoder.encode(destination, "UTF-8"); // 인코딩된 도착지명

        // URLEncode will encode user input to URL format
        Log.d(TAG, "보내는 주소 값 : " + DIRECTION_URL_API
                + "origin=" + urlOrigin
                + "&destination=" + urlDestination
                + "&mode=transit"
                + "&key=" + GOOGLE_API_KEY);

        // 서버로 보낼 URL 주소 값
        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&mode=transit" + "&key=" + GOOGLE_API_KEY;

    }

    // link를 얻고 나서 쓰레드 실행 => raw data를 다운받는데 시간이 많이 소모되기 때문에
    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];

            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream(); // get the result string data
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;

                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        // After completely downloading data, on PostExecute will be processed
        @Override
        protected void onPostExecute(String res) {

            try {
                parseJson(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    } // AsyncTask finish

    private void parseJson(String data) throws JSONException {

        if (data == null) {
            return;
        }


        List<Routes> routes = new ArrayList<Routes>();
        JSONObject jsonData = new JSONObject(data); // will create a JSONObject from a data String (즉, json 형태의 값들)

        // json data에서 우리는 "routes" 객체에 담겨 있는 데이터가 필요하다. (JSONArray start with [ )
        // JSONObject에서 "routes"로 시작하는 데이터를 JSONArray가 얻는다.

        JSONArray jsonRoutes = jsonData.getJSONArray("routes"); // 최상위 JSONArray

        for (int i = 0; i < jsonRoutes.length(); i++) {

            JSONObject jsonRoute = jsonRoutes.getJSONObject(i); // For each array objects, get each JSONObject
            Routes route = new Routes();

            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);

            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");


            // 데이터 담기
            route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value")); // distance : { "text":"1 km", "value":250 }
            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value")); // duration : { "text":"1 mins", "value":205}

            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");

            // latitude(위도), longtitude(경도)
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng")); // "start_location" : { "lat": 40.1238098, "lng": -73.134554}
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng")); // "end_location" : { "lat": 40.1610498, "lng": -73.134355}

            route.points = decodePolyLine(overview_polylineJson.getString("points")); // polyline decode

            routes.add(route); // it will return List<Routes> routes

        }

        listener.onDirectionFinderSuccess(routes);

    } // parseJson() finish


    // 폴리라인 decode
    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

}














// JSon 데이터 파싱 받을 때 경로 지점 모두를 받기 위한 함수
//    /**
//     * Receives a JSONObject and returns a list of lists containing latitude and longitude
//     */
//    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
//
//        List<List<HashMap<String, String>>> routes = new ArrayList<>();
//        List<Routes> route = new ArrayList<Routes>();
//
//
//        JSONArray jRoutes;
//        JSONArray jLegs;
//        JSONArray jSteps;
//
//        try {
//
//            jRoutes = jObject.getJSONArray("routes"); // 경로
//
//            /** Traversing all routes */
//            for (int i = 0; i < jRoutes.length(); i++) {
//                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
//                List path = new ArrayList<>();
//
//                /** Traversing all legs */
//                // steps안에 구간구간의 정보들이 모두 담겨져 있다.
//                // distance, duration,
//                for (int j = 0; j < jLegs.length(); j++) {
//                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
//
//                    /** Traversing all steps */
//                    for (int k = 0; k < jSteps.length(); k++) {
//                        JSONObject jsonStep = jSteps.getJSONObject(i); // For each array objects, get each JSONObject
//
//                        JSONObject jsonEndLocation = jsonStep.getJSONObject("end_location");
//                        JSONObject jsonHtmlInstrument = jsonStep.getJSONObject("jsonHtmlInstrument");
//
//                        route. = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng")); // "start_location" : { "lat": 40.1238098, "lng": -73.134554}
//
//
//                        String polyline = "";
//                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
//                        List<LatLng> list = decodePolyLine(polyline);
//
//                        /** Traversing all points */
//                        for (int l = 0; l < list.size(); l++) {
//                            HashMap<String, String> hm = new HashMap<>();
//                            hm.put("lat", Double.toString((list.get(l)).latitude));
//                            hm.put("lng", Double.toString((list.get(l)).longitude));
//                            path.add(hm);
//                        }
//                    }
//                    routes.add(path);
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//        }
//
//
//        return routes;
//    }
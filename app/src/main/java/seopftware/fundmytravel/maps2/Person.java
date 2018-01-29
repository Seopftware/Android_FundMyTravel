package seopftware.fundmytravel.maps2;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Peter-John on 2017-04-11.
 * GoogleMapsTutorial
 */

public class Person implements ClusterItem {

    private final LatLng mPosition;
    private String name;
    private int resourceId; // drawable id

    // 방 고유 ID 번호 저장
    String room_id;

    // 방 참가자 수
    int room_numpeople;

    // 방 이름
    String room_name_title;

    // 방 태그
    String room_name_tag;

    // 방 생성한 사람 이름
    String room_name_streamer;

    // 방송 메인 사진
    String room_image_path;

    // 방송 여부
    String room_status; // true면 방송 중, false면 방송 종료 (VOD시청으로)


    public Person(double lat, double lng, String name, int resourceId) {
        this.name = name;
        this.resourceId = resourceId;
        mPosition = new LatLng(lat, lng);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getmPosition() {
        return mPosition;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public int getRoom_numpeople() {
        return room_numpeople;
    }

    public void setRoom_numpeople(int room_numpeople) {
        this.room_numpeople = room_numpeople;
    }

    public String getRoom_name_title() {
        return room_name_title;
    }

    public void setRoom_name_title(String room_name_title) {
        this.room_name_title = room_name_title;
    }

    public String getRoom_name_tag() {
        return room_name_tag;
    }

    public void setRoom_name_tag(String room_name_tag) {
        this.room_name_tag = room_name_tag;
    }

    public String getRoom_name_streamer() {
        return room_name_streamer;
    }

    public void setRoom_name_streamer(String room_name_streamer) {
        this.room_name_streamer = room_name_streamer;
    }

    public String getRoom_image_path() {
        return room_image_path;
    }

    public void setRoom_image_path(String room_image_path) {
        this.room_image_path = room_image_path;
    }

    public String getRoom_status() {
        return room_status;
    }

    public void setRoom_status(String room_status) {
        this.room_status = room_status;
    }
}

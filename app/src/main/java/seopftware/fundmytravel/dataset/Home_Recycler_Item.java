package seopftware.fundmytravel.dataset;

/**
 * Created by MSI on 2018-01-03.
 */

// Home화면의 친구 목록을 불러오기 위한 데이터들
public class Home_Recycler_Item {

    int home_type; // View Type 지정
    int home_id; // 유저들의 고유 Id 저장
    String home_profile; // 이미지 경로
    String home_nickname; // 유저들의 닉네임
    String home_message; // 유저들의 상태 메세지

    public int getHome_type() {
        return home_type;
    }

    public void setHome_type(int home_type) {
        this.home_type = home_type;
    }

    public int getHome_id() {
        return home_id;
    }

    public void setHome_id(int home_id) {
        this.home_id = home_id;
    }

    public String getHome_profile() {
        return home_profile;
    }

    public void setHome_profile(String home_profile) {
        this.home_profile = home_profile;
    }

    public String getHome_nickname() {
        return home_nickname;
    }

    public void setHome_nickname(String home_nickname) {
        this.home_nickname = home_nickname;
    }

    public String getHome_message() {
        return home_message;
    }

    public void setHome_message(String home_message) {
        this.home_message = home_message;
    }
}

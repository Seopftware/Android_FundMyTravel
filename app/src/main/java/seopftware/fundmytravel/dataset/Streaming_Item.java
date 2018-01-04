package seopftware.fundmytravel.dataset;

/**
 * Created by MSI on 2018-01-03.
 */

// Home화면의 친구 목록을 불러오기 위한 데이터들
public class Streaming_Item {

    int streaming_type; // View Type 지정
    String streaming_user_id; // 유저들의 고유 Id 저장
    String streaming_user_nickname; // 유저들의 닉네임
    String streaming_user_message; // 유저가 보낸 메세지
    String streaming_message_time; // 채팅 메세지 보낸 시간
    String streameing_image_profile; // 유저 이미지 프로필
    String streaming_image_star; // 별풍선 효과
    String streaming_image_chat; // 채팅 중 이미지 (이모티콘도 보내고 싶다! 이 부분은 나중에)

    public int getStreaming_type() {
        return streaming_type;
    }

    public void setStreaming_type(int streaming_type) {
        this.streaming_type = streaming_type;
    }

    public String getStreaming_user_id() {
        return streaming_user_id;
    }

    public void setStreaming_user_id(String streaming_user_id) {
        this.streaming_user_id = streaming_user_id;
    }

    public String getStreaming_user_nickname() {
        return streaming_user_nickname;
    }

    public void setStreaming_user_nickname(String streaming_user_nickname) {
        this.streaming_user_nickname = streaming_user_nickname;
    }

    public String getStreaming_user_message() {
        return streaming_user_message;
    }

    public void setStreaming_user_message(String streaming_user_message) {
        this.streaming_user_message = streaming_user_message;
    }

    public String getStreaming_message_time() {
        return streaming_message_time;
    }

    public void setStreaming_message_time(String streaming_message_time) {
        this.streaming_message_time = streaming_message_time;
    }

    public String getStreameing_image_profile() {
        return streameing_image_profile;
    }

    public void setStreameing_image_profile(String streameing_image_profile) {
        this.streameing_image_profile = streameing_image_profile;
    }

    public String getStreaming_image_star() {
        return streaming_image_star;
    }

    public void setStreaming_image_star(String streaming_image_star) {
        this.streaming_image_star = streaming_image_star;
    }

    public String getStreaming_image_chat() {
        return streaming_image_chat;
    }

    public void setStreaming_image_chat(String streaming_image_chat) {
        this.streaming_image_chat = streaming_image_chat;
    }
}

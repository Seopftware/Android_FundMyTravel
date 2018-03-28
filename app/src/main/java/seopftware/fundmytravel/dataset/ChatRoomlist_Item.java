package seopftware.fundmytravel.dataset;

/**
 * Created by MSI on 2018-01-03.
 */

// Home화면의 채팅 목록을 불러오기 위한 데이터들
public class ChatRoomlist_Item {

    int user_key; // 나의 고유 번호

    int receive_id; // 메세지를 받는 상대방

    // 유저 닉네임 (메세지를 받을)
    String user_name;

    // 메세지 받은 시간 비교
    String message_time;

    // 메세지 아이콘
    String message_icon;

    // 메세지 상태 (메세지를 열었는지 안열었는지 여부)
    String message_status;


    public int getUser_key() {
        return user_key;
    }

    public void setUser_key(int user_key) {
        this.user_key = user_key;
    }

    public int getReceive_id() {
        return receive_id;
    }

    public void setReceive_id(int receive_id) {
        this.receive_id = receive_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMessage_time() {
        return message_time;
    }

    public void setMessage_time(String message_time) {
        this.message_time = message_time;
    }

    public String getMessage_icon() {
        return message_icon;
    }

    public void setMessage_icon(String message_icon) {
        this.message_icon = message_icon;
    }

    public String getMessage_status() {
        return message_status;
    }

    public void setMessage_status(String message_status) {
        this.message_status = message_status;
    }
}
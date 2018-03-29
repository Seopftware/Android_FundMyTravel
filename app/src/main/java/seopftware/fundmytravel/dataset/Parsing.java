package seopftware.fundmytravel.dataset;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Retrofit 데이터 셋 클래스
 * @author 김인섭
 * @version 1.0.0
 * @since 2018-01-06 오전 11:55
 * @class comment
 *   이 DataSet은 유저 정보를 Retrofit을 통해 받아 오기 위한 용도로 만들어졌습니다.
 *   데이터 형태
 *   {"result":[{"user_key":"51","user_method":"phone","user_email":"0","user_phone":"01041675164","user_name":"MACHINE","user_photo":"inseop.jpg"}]}
 **/

// gson으로 변경해 주는 사이트
// http://www.jsonschema2pojo.org/
// source type: JSON, Annotation style : Gson


public class Parsing {

    @SerializedName("result")
    @Expose
    private List<Result> result = null;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }


    // =========================================================================================================
    // 유저 정보 GSon
    // 정보를 받아오는 Table: user_info
    // =========================================================================================================
    public class Result{

        @SerializedName("user_key") // 고유 ID 번호
        @Expose
        private int userKey;

        @SerializedName("user_method") // 회원가입 방법(폰, 구글, 네이버)
        @Expose
        private String userMethod;

        @SerializedName("user_email") // 유저 이메일
        @Expose
        private String userEmail;

        @SerializedName("user_phone") // 유저 폰번호
        @Expose
        private String userPhone;

        @SerializedName("user_name") // 유저 닉네임
        @Expose
        private String userName;

        @SerializedName("user_photo") // 유저 사진
        @Expose
        private String userPhoto;

        @SerializedName("user_status") // 유저 상태 메세지
        @Expose
        private String userStatus;


        @SerializedName("user_photo_background") // 유저 백그라운드 이미지
        @Expose
        private String userPhotoBackground;

        public int getUserKey() {
            return userKey;
        }

        public void setUserKey(int userKey) {
            this.userKey = userKey;
        }

        public String getUserMethod() {
            return userMethod;
        }

        public void setUserMethod(String userMethod) {
            this.userMethod = userMethod;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getUserPhone() {
            return userPhone;
        }

        public void setUserPhone(String userPhone) {
            this.userPhone = userPhone;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserPhoto() {
            return userPhoto;
        }

        public void setUserPhoto(String userPhoto) {
            this.userPhoto = userPhoto;
        }

        public String getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(String userStatus) {
            this.userStatus = userStatus;
        }

        public String getUserPhotoBackground() {
            return userPhotoBackground;
        }

        public void setUserPhotoBackground(String userPhotoBackground) {
            this.userPhotoBackground = userPhotoBackground;
        }
    }

    @SerializedName("friendslist")
    @Expose
    private List<Friendslist> friendslist = null;

    public List<Friendslist> getFriendslist() {
        return friendslist;
    }

    public int getFriendCount() {
        return friendslist.size();
    }


    public void setFriendslist(List<Friendslist> friendslist) {
        this.friendslist = friendslist;
    }

    // =========================================================================================================
    // 친구 목록 + 친구 정보 GSon
    // 정보를 받아오는 Table: user_info, user_friends
    // user_friends AS f LEFT JOIN user_info AS i
    // =========================================================================================================
    public class Friendslist {

        @SerializedName("user_login_id") // 나의 고유 ID
        @Expose
        private int userLoginId;

        @SerializedName("user_friends_id") // 친구의 고유 ID
        @Expose
        private int userFriendsId;

        @SerializedName("user_name") // 친구의 닉네임
        @Expose
        private String userName;

        @SerializedName("user_photo") // 친구의 프로필 사진
        @Expose
        private String userPhoto;

        @SerializedName("user_status") // 친구의 상태 메세지
        @Expose
        private String userStatus;


        public int getUserLoginId() {
            return userLoginId;
        }

        public void setUserLoginId(int userLoginId) {
            this.userLoginId = userLoginId;
        }

        public int getUserFriendsId() {
            return userFriendsId;
        }

        public void setUserFriendsId(int userFriendsId) {
            this.userFriendsId = userFriendsId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserPhoto() {
            return userPhoto;
        }

        public void setUserPhoto(String userPhoto) {
            this.userPhoto = userPhoto;
        }

        public String getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(String userStatus) {
            this.userStatus = userStatus;
        }
    }
    // =========================================================================================================



    // =========================================================================================================
    // 방 정보 GSon
    // 방 정보를 받아오는 Table: streaming_roomlist
    // =========================================================================================================
    @SerializedName("roomlist")
    @Expose
    private List<Roomlist> roomlist = null;

    public List<Roomlist> getRoomlist() {
        return roomlist;
    }


    public int getRoomCount() {
        return roomlist.size();
    }



    public void setRoomlist(List<Roomlist> roomlist) {
        this.roomlist = roomlist;
    }

    public class Roomlist {

        @SerializedName("room_id")
        @Expose
        private String roomId;
        @SerializedName("room_numpeople")
        @Expose
        private int roomNumpeople;
        @SerializedName("room_name_title")
        @Expose
        private String roomNameTitle;
        @SerializedName("room_name_tag")
        @Expose
        private String roomNameTag;
        @SerializedName("room_name_streamer")
        @Expose
        private String roomNameStreamer;
        @SerializedName("room_image_path")
        @Expose
        private String roomImagePath;
        @SerializedName("room_status")
        @Expose
        private String roomStatus;
        @SerializedName("room_location")
        @Expose
        private String roomLocation;

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public int getRoomNumpeople() {
            return roomNumpeople;
        }

        public void setRoomNumpeople(int roomNumpeople) {
            this.roomNumpeople = roomNumpeople;
        }

        public String getRoomNameTitle() {
            return roomNameTitle;
        }

        public void setRoomNameTitle(String roomNameTitle) {
            this.roomNameTitle = roomNameTitle;
        }

        public String getRoomNameTag() {
            return roomNameTag;
        }

        public void setRoomNameTag(String roomNameTag) {
            this.roomNameTag = roomNameTag;
        }

        public String getRoomNameStreamer() {
            return roomNameStreamer;
        }

        public void setRoomNameStreamer(String roomNameStreamer) {
            this.roomNameStreamer = roomNameStreamer;
        }

        public String getRoomImagePath() {
            return roomImagePath;
        }

        public void setRoomImagePath(String roomImagePath) {
            this.roomImagePath = roomImagePath;
        }

        public String getRoomStatus() {
            return roomStatus;
        }

        public void setRoomStatus(String roomStatus) {
            this.roomStatus = roomStatus;
        }

        public String getRoomLocation() {
            return roomLocation;
        }

        public void setRoomLocation(String roomLocation) {
            this.roomLocation = roomLocation;
        }
    }
    // =========================================================================================================

    // =========================================================================================================
    // 채팅 메세지 GSon
    // 방 정보를 받아오는 테이블 message_normal
    // =========================================================================================================

    @SerializedName("chatlist")
    @Expose
    private List<Chatlist> chatlist = null;

    public List<Chatlist> getChatlist() {
        return chatlist;
    }

    public void setChatlist(List<Chatlist> chatlist) {
        this.chatlist = chatlist;
    }

    public int getChatCount() {
        return chatlist.size();
    }

    // =========================================================================================================
    public class Chatlist {

        @SerializedName("sender_name")
        @Expose
        private String senderName;
        @SerializedName("sender_profile")
        @Expose
        private String senderProfile;
        @SerializedName("sender_message")
        @Expose
        private String senderMessage;
        @SerializedName("broadcast_time")
        @Expose
        private String broadcastTime;

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getSenderProfile() {
            return senderProfile;
        }

        public void setSenderProfile(String senderProfile) {
            this.senderProfile = senderProfile;
        }

        public String getSenderMessage() {
            return senderMessage;
        }

        public void setSenderMessage(String senderMessage) {
            this.senderMessage = senderMessage;
        }

        public String getBroadcastTime() {
            return broadcastTime;
        }

        public void setBroadcastTime(String broadcastTime) {
            this.broadcastTime = broadcastTime;
        }

    }

    // =========================================================================================================
    // 채팅 방 정보 GSon
    // 채팅 방 정보를 받아오는 Table: message_room
    // =========================================================================================================
        @SerializedName("chatroomlist")
        @Expose
        private List<Chatroomlist> chatroomlist = null;

        public List<Chatroomlist> getChatroomlist() {
            return chatroomlist;
        }

        public void setChatroomlist(List<Chatroomlist> chatroomlist) {
            this.chatroomlist = chatroomlist;
        }

        public int getChatRoomCount() {
            return chatroomlist.size();
        }

        // =========================================================================================================

    public class Chatroomlist {

        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("receiver_id")
        @Expose
        private String receiverId;
        @SerializedName("message_time")
        @Expose
        private String messageTime;
        @SerializedName("message_status")
        @Expose
        private String messageStatus;
        @SerializedName("receiver_name")
        @Expose
        private String receiverName;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverId) {
            this.receiverId = receiverId;
        }

        public String getMessageTime() {
            return messageTime;
        }

        public void setMessageTime(String messageTime) {
            this.messageTime = messageTime;
        }

        public String getMessageStatus() {
            return messageStatus;
        }

        public void setMessageStatus(String messageStatus) {
            this.messageStatus = messageStatus;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

}



}

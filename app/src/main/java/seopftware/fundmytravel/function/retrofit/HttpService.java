package seopftware.fundmytravel.function.retrofit;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import seopftware.fundmytravel.dataset.Parsing;

/**
 * Retrofit 호출 interface
 * @author 김인섭
 * @version 1.0.0
 * @since 2018-01-06 오후 12:02
 * @class comment
 *   이 인터페이스는 Retrofit을 사용하기 위한 용도로 만들어졌습니다.
**/

public interface HttpService {

    // PHP에서 POST로 받아올 변수명

    // 폰 번호로 회원가입할 때
    @FormUrlEncoded
    @POST("php/insert/register_phone.php") // 폴더명/파일명
    Call<ResponseBody>register_phone(
            // @Field("서버에 보낼 변수명")
            @Field("user_method") String user_method, // 회원가입 방법
            @Field("user_phone") String user_phone // 회원가입할 휴대폰 번호
    );

    // 나의 id 보내고 내 정보 가져오기
    @FormUrlEncoded
    @POST("php/select/user_info.php") // 폴더명/파일명
    Call<Parsing>get_userinfo(
            @Field("user_key") int user_key // 유저 고유 ID
    );


    // 나의 id 보내고 친구 목록 가져오기
    @FormUrlEncoded
    @POST("php/select/find_friendlist.php") // 폴더명/파일명
    Call<Parsing>get_friendslist(
            @Field("user_login_id") int user_login_id
    );

    // 나의 id 보내고 친구 목록 가져오기
    @FormUrlEncoded
    @POST("php/select/find_roomlist.php") // 폴더명/파일명
    Call<Parsing>get_roomlist(
            @Field("user_login_id") int user_login_id
    );

    // 나의 id 보내고 채팅 목록 가져오기
    @FormUrlEncoded
    @POST("php/select/find_chatlist.php") // 폴더명/파일명
    Call<Parsing>get_chatlist(
            @Field("user_key") int user_key // 유저 고유 ID
    );

    // Pic Message
    // Retrofit을 통해 서버로 사진을 전송하기 위한 부분
    @Multipart
    @POST("php/insert/add_pic_message.php")
    Call<ResponseBody>uploadPhoto(

            @Part MultipartBody.Part photo,
            @Part("sender_id") int sender_id, // 사진 메세지를 보내는 사람의 ID
            @Part("receiver_id") int receiver_id, // 사진 메세지를 받는 사람의 ID
            @Part("message_limited_time") String message_limited_time, // 상대방이 사진을 보는 순간 메세지가 사라지는데 걸리는 시간
            @Part("message_date") String message_date // 메세지를 보낸 시간
    );

    // Streaming Room Info
    // 스트리밍 방 정보를 서버로 보내 DB에 등록
    // Retrofit을 통해 서버로 사진을 전송하기 위한 부분
    @Multipart
    @POST("php/insert/add_roominfo.php")
    Call<ResponseBody>upload_roominfo(

            @Part MultipartBody.Part photo,
            @Part("room_id") String room_id, // 방 고유 ID 저장
            @Part("room_numpeople") int room_numpeople, // 방 참가자 수
            @Part("room_name_title") String room_name_title, // 방 이름
            @Part("room_name_tag") String message_date, // 방 태그
            @Part("room_name_streamer") String room_name_streamer, // 스트리머 이름
            @Part("room_image_path") String room_image_path, // 방송 메인 사진 이름
            @Part("room_status") String room_status, // 방송 여부 표시 (LIVE, VOD)
            @Part("room_location") String room_location // 방송 좌표
    );


    // 채팅 메세지 저장
    @FormUrlEncoded
    @POST("php/insert/save_chatmessage.php") // 폴더명/파일명
    Call<ResponseBody>save_chatmessage(
            // @Field("서버에 보낼 변수명")
            @Field("room_id") String room_id, // 방의 고유 아이디
            @Field("sender_name") String sender_name, // 메세지 보낸 사람의 이름
            @Field("sender_profile") String sende_profile, // 보낸 사람의 프로필 사진 경로
            @Field("sender_message") String sender_message, // 메세지 내용
            @Field("broadcast_time") String broadcast_time // 메세지를 보냈을 때의 방송 시간
    );

    // 채팅 메세지 불러오기
    // 서버로 부터 Json 데이터 값을 가져온다.
    @FormUrlEncoded
    @POST("php/select/get_chatlist.php") // 폴더명/파일명
    Call<Parsing>get_chatlist(
            // @Field("서버에 보낼 변수명")
            @Field("room_id") String room_id // 방의 고유 아이디
    );


    // 생방송 끝나면 RDB에 접근해서 LIVE -> VOD 로 바꿔주기
    @FormUrlEncoded
    @POST("php/update/update_roomstatus.php") // 폴더명/파일명
    Call<ResponseBody>update_roomstatus(
            // @Field("서버에 보낼 변수명")
            @Field("room_id") String room_id // 방 번호를 보낸다.
    );

    // 별풍선 갯수 업데이트 해주는 곳
    // 폰 번호로 회원가입할 때
    @FormUrlEncoded
    @POST("php/update/update_star.php") // 폴더명/파일명
    Call<ResponseBody>update_star(
            @Field("streamer_name") String streamer_name, // 코인을 보내고자 하는 대상
            @Field("send_money") int send_money, // 보내고자 하는 코인의 갯수
            @Field("user_key") String sender_name, // 코인을 보내고자 하는 대상
            @Field("room_id") String room_id // 코인을 보내고자 하는 대상
    );


    // 별풍선 갯수 업데이트 해주는 곳
    // 폰 번호로 회원가입할 때
    @FormUrlEncoded
    @POST("php/update/update_roominfo.php") // 폴더명/파일명
    Call<ResponseBody>update_roominfo(
            @Field("room_id") String room_id, // 방의 고유 아이디\

            // 방에 접속한 사람의 성별
            @Field("room_male") int room_male,
            @Field("room_female") int room_female,

            // 방에 접속한 사람의 연령대
            @Field("room_20s") int room_20s,
            @Field("room_30s") int room_30s,
            @Field("room_40s") int room_40s,
            @Field("room_50s") int room_50s
    );
}

/**
 *
 어노테이션	설명
 @Path	API 엔드포인트의 변수를 대채
 @Query	어노테이션의 매개변수 값으로 쿼리 키 이름을 지정
 @Body	Post 호출의 페이로드
 @Header	어노테이션의 매개변수 값으로 헤더를 지정
 */
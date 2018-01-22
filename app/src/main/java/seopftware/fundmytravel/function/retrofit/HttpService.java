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
            @Field("user_phone") String user_phone); // 회원가입할 휴대폰 번호



    // 나의 id 보내고 내 정보 가져오기
    @FormUrlEncoded
    @POST("php/select/user_info.php") // 폴더명/파일명
    Call<Parsing>get_userinfo(
            @Field("user_key") int user_key); // 유저 고유 ID


    // 나의 id 보내고 친구 목록 가져오기
    @FormUrlEncoded
    @POST("php/select/find_friendlist.php") // 폴더명/파일명
    Call<Parsing>get_friendslist(
            @Field("user_login_id") int user_login_id);


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



}

/**
 *
 어노테이션	설명
 @Path	API 엔드포인트의 변수를 대채
 @Query	어노테이션의 매개변수 값으로 쿼리 키 이름을 지정
 @Body	Post 호출의 페이로드
 @Header	어노테이션의 매개변수 값으로 헤더를 지정
 */
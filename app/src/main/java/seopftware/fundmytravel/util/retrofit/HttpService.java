package seopftware.fundmytravel.util.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by MSI on 2018-01-03.
 */

public interface HttpService {


    // 폰 번호로 회원가입할 때
    @FormUrlEncoded
    @POST("php/register/phone.php") // 폴더명/파일명
    Call<ResponseBody>register_phone(
            @Field("user_method") String user_login, // 회원가입 방법
            @Field("user_phone") String user_phone); // 회원가입할 휴대폰 번호

}

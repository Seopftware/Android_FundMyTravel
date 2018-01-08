package seopftware.fundmytravel.function.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static seopftware.fundmytravel.function.MyApp.createOkHttpClient;


public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://52.79.138.20/") // 서버 주소
                    .addConverterFactory(GsonConverterFactory.create()) // Gson을 통해 Json 변환
                    .client(createOkHttpClient()) // 서버 통신 과정을 보기 위한 okHttp3 호출
                    .build();
        }
        return retrofit;
    }
}

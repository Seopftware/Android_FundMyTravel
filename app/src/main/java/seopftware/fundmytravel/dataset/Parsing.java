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


    public class Result{

        @SerializedName("user_key")
        @Expose
        private String userKey;
        @SerializedName("user_method")
        @Expose
        private String userMethod;
        @SerializedName("user_email")
        @Expose
        private String userEmail;
        @SerializedName("user_phone")
        @Expose
        private String userPhone;
        @SerializedName("user_name")
        @Expose
        private String userName;
        @SerializedName("user_photo")
        @Expose
        private String userPhoto;

        public String getUserKey() {
            return userKey;
        }

        public void setUserKey(String userKey) {
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
    }


}

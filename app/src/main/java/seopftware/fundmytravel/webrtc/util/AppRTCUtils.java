package seopftware.fundmytravel.webrtc.util;

import android.os.Build;
import android.util.Log;

/**
 * @author 김인섭
 * @version 1.0.0
 * @since 2018-01-08 오전 11:37
 * @class comment
 * 이 클래스는 WebRTC에서 자주 불러야 하는 함수들을 저장해 놓은 곳 입니다
 **/
public final class AppRTCUtils {
    private AppRTCUtils() {

    }

    // Helper method which throws an exception. when an assertion has failed.
    public static void assertIsTrue(boolean condition) {

        if (!condition) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    // Helper method for building a string of thread information
    public static String getThreadInfo() {
        return "@[name=" + Thread.currentThread().getName() + ", id=" + Thread.currentThread().getId()
                + "]";
    }

    public static void logDeviceInfo(String tag) {
        Log.d(tag, "Android SDK: " + Build.VERSION.SDK_INT + ", "
                + "Release: " + Build.VERSION.RELEASE + ", "
                + "Brend: " + Build.BRAND + ", "
                + "Device: " + Build.DEVICE + ", "
                + "Id: " + Build.ID + ", "
                + "Hardware: " + Build.HARDWARE + ", "
                + "Manufacturer: " + Build.MANUFACTURER + ", "
                + "Model: " + Build.MODEL + ", "
                + "Product: " + Build.PRODUCT);
    }
}

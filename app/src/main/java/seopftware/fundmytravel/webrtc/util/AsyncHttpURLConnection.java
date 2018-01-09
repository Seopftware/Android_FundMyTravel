package seopftware.fundmytravel.webrtc.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by MSI on 2018-01-08.
 */

public class AsyncHttpURLConnection {

    public static final String EXTRA_ROOMID = "org.appspot.apprtc.ROOMID";
    public static final String EXTRA_URLPARAMETERS = "org.appspot.apprtc.URLPARAMETERS";
    public static final String EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK";
    public static final String EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL";
    public static final String EXTRA_SCREENCAPTURE = "org.appspot.apprtc.SCREENCAPTURE";
    public static final String EXTRA_CAMERA2 = "org.appspot.apprtc.CAMERA2";
    public static final String EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS";
    public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED =
            "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER";
    public static final String EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE";
    public static final String EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC";
    public static final String EXTRA_HWCODEC_ENABLED = "org.appspot.apprtc.HWCODEC";
    public static final String EXTRA_CAPTURETOTEXTURE_ENABLED = "org.appspot.apprtc.CAPTURETOTEXTURE";
    public static final String EXTRA_FLEXFEC_ENABLED = "org.appspot.apprtc.FLEXFEC";
    public static final String EXTRA_AUDIO_BITRATE = "org.appspot.apprtc.AUDIO_BITRATE";
    public static final String EXTRA_AUDIOCODEC = "org.appspot.apprtc.AUDIOCODEC";
    public static final String EXTRA_NOAUDIOPROCESSING_ENABLED =
            "org.appspot.apprtc.NOAUDIOPROCESSING";
    public static final String EXTRA_AECDUMP_ENABLED = "org.appspot.apprtc.AECDUMP";
    public static final String EXTRA_OPENSLES_ENABLED = "org.appspot.apprtc.OPENSLES";
    public static final String EXTRA_DISABLE_BUILT_IN_AEC = "org.appspot.apprtc.DISABLE_BUILT_IN_AEC";
    public static final String EXTRA_DISABLE_BUILT_IN_AGC = "org.appspot.apprtc.DISABLE_BUILT_IN_AGC";
    public static final String EXTRA_DISABLE_BUILT_IN_NS = "org.appspot.apprtc.DISABLE_BUILT_IN_NS";
    public static final String EXTRA_ENABLE_LEVEL_CONTROL = "org.appspot.apprtc.ENABLE_LEVEL_CONTROL";
    public static final String EXTRA_DISABLE_WEBRTC_AGC_AND_HPF =
            "org.appspot.apprtc.DISABLE_WEBRTC_GAIN_CONTROL";
    public static final String EXTRA_DISPLAY_HUD = "org.appspot.apprtc.DISPLAY_HUD";
    public static final String EXTRA_TRACING = "org.appspot.apprtc.TRACING";
    public static final String EXTRA_CMDLINE = "org.appspot.apprtc.CMDLINE";
    public static final String EXTRA_RUNTIME = "org.appspot.apprtc.RUNTIME";
    public static final String EXTRA_VIDEO_FILE_AS_CAMERA = "org.appspot.apprtc.VIDEO_FILE_AS_CAMERA";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_WIDTH";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT";
    public static final String EXTRA_USE_VALUES_FROM_INTENT =
            "org.appspot.apprtc.USE_VALUES_FROM_INTENT";
    public static final String EXTRA_DATA_CHANNEL_ENABLED = "org.appspot.apprtc.DATA_CHANNEL_ENABLED";
    public static final String EXTRA_ORDERED = "org.appspot.apprtc.ORDERED";
    public static final String EXTRA_MAX_RETRANSMITS_MS = "org.appspot.apprtc.MAX_RETRANSMITS_MS";
    public static final String EXTRA_MAX_RETRANSMITS = "org.appspot.apprtc.MAX_RETRANSMITS";
    public static final String EXTRA_PROTOCOL = "org.appspot.apprtc.PROTOCOL";
    public static final String EXTRA_NEGOTIATED = "org.appspot.apprtc.NEGOTIATED";
    public static final String EXTRA_ID = "org.appspot.apprtc.ID";

    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;

    // List of mandatory application permissions.
    private static final String[] MANDATORY_PERMISSIONS = {"android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO", "android.permission.INTERNET"};

    // Peer connection statistics callback period in ms.
    private static final int STAT_CALLBACK_PERIOD = 1000;

    private static final int HTTP_TIMEOUT_MS = 8000;
    private static final String HTTP_ORIGIN = "https://appr.tc";
    private final String method;
    private final String url;
    private final String message;
    private final AsyncHttpEvents events;
    private String contentType;

    // Http requests callbacks
    public interface AsyncHttpEvents {
        void onHttpError(String errorMessage);
        void onHttpComplete(String response);
    }

    public AsyncHttpURLConnection(String method, String url, String message, AsyncHttpEvents events) {

        this.method=method;
        this.url=url;
        this.message=message;
        this.events=events;
    }

    public void setContentType(String contentType) {
        this.contentType=contentType;
    }

    public void send() {
        Runnable runHttp = new Runnable() {
            @Override
            public void run() {
                sendHttpMessage();
            }
        };
        new Thread(runHttp).start();
    }

    private void sendHttpMessage() {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            byte[] postData = new byte[0];
            if (message != null) {
                postData = message.getBytes("UTF-8");
            }

            connection.setRequestMethod(method);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setConnectTimeout(HTTP_TIMEOUT_MS);
            connection.setReadTimeout(HTTP_TIMEOUT_MS);
            connection.addRequestProperty("origin", HTTP_ORIGIN); // Query request origin from pref_room_server_url_key preferences.

            boolean doOutput = false;
            if (method.equals("POST")) {
                doOutput = true;
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(postData.length);
            }

            if (contentType == null) {
                connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            } else {
                connection.setRequestProperty("Content-Type", contentType);
            }

            //Send POST request
            if (doOutput && postData.length > 0) {
                OutputStream outStream = connection.getOutputStream();
                outStream.write(postData);
                outStream.close();
            }

            // Get response
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                events.onHttpError("Non-200 response to " + method + "to URL: " + url + " : "
                        + connection.getHeaderField(null));
                connection.disconnect();
                return;
            }

            InputStream responseStream = connection.getInputStream();
            String response = drainStream(responseStream);
            responseStream.close();
            connection.disconnect();
            events.onHttpComplete(response);
        } catch (SocketTimeoutException e) {
            events.onHttpError("HTTP " + method + " to " + url + " timeout");
        } catch (IOException e) {
            events.onHttpError("HTTP " + method + " to " + url + " error: " + e.getMessage());
        }
    }

        // Return the contents of an InputStream as a String
        private static String drainStream(InputStream in) {
            Scanner s = new Scanner(in).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
    }

}

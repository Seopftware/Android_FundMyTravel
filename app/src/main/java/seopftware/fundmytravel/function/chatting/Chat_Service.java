package seopftware.fundmytravel.function.chatting;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import static seopftware.fundmytravel.function.MyApp.BROADCAST_NETTY_MESSAGE;
import static seopftware.fundmytravel.function.MyApp.NETTY_PORT;
import static seopftware.fundmytravel.function.MyApp.SERVER_IP;

/**
 * 채팅 서버와의 통신을 위한 서비스
 * @author 김인섭
 * @version 1.0.0
 * @class comment
 * @since 2018-01-05 오전 10:04
 * 이 클래스는 네티 채팅 서버와 서비스 단에서 통신하기 위한 용도로 만들어졌습니다.
 **/

public class Chat_Service extends Service {

    private static final String TAG = "all_" + "Chat_Service";


    public static Channel channel; // 전역변수로 만들어서 Service에서 생성된 Netty channel을 활용해 서버로 데이터 전송 가능)
    Bootstrap bootstrap;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "**************************************************");
        Log.d(TAG, "서비스 on Create(): 1.Netty Chat Server Start");
        Log.d(TAG, "**************************************************");
        new ChatClient(SERVER_IP, NETTY_PORT).run();

    }

    // =========================================================================================================
    // Netty Engine
    // =========================================================================================================

    // 서버와 연결하기 위한 클래스
    class ChatClient extends Thread {

        private final String host;
        private final int port;

        public ChatClient(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public void run() {
            EventLoopGroup group = new NioEventLoopGroup();

            try {

                Log.d(TAG, "**************************************************");
                Log.d(TAG, "서비스 ChatClient() : 2. Netty 서버와 채널(소켓) 연결");
                Log.d(TAG, "**************************************************");
                // to set up a channel
                bootstrap = new Bootstrap()
                        .group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChatClientInitializer());

                // 서버에 최초 접속
                channel = bootstrap.connect(host, port).sync().channel();
                // channel.writeAndFlush("서버와 연결되었습니다."); // 서버로 보내는 메세지(입장 메세지로?)
                // 소켓이 연결되고 안되고로 채팅방 입장/퇴장 구분하기

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 메세지를 thread 단에서 서버로 보내는 작업
    public class ChatClientInitializer extends ChannelInitializer<SocketChannel> {

        static final int MESSAGE_SIZE = 8192;

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            // Here's define what Netty calls a pipeline
            // It basically describes how we want to organize our communication

            ChannelPipeline pipeline = ch.pipeline();

            // First, tell netty we're expecting frames of at most 8192 in size, each delimited with line endings
            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(MESSAGE_SIZE, Delimiters.lineDelimiter()));

            // Since we're just exchanging Strings between the server and clients, we can use the StringDecoder to decode received bytes into Strings.
            pipeline.addLast("decoder", new StringDecoder());

            // StringEncoder to encode Strings into bytes, which we can then send over to the server
            pipeline.addLast("encoder", new StringEncoder());

            // finally, define a class which will handle all the decoded incoming Strings from the server
            pipeline.addLast("handler", new ChatClientHandler());

        }
    }

    // this class to handle incoming String objects
    // 메세지 받는 곳
    public class ChatClientHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, final String msg) throws Exception {
            // To print any String message we receive from the server to the console
            // 서버로 부터 받은 메세지

            Log.d(TAG, "****************************************************************");
            Log.d(TAG, "받은 메세지");
            Log.d(TAG, "ctx : " + ctx);
            Log.d(TAG, "msg : " + msg);
            Log.d(TAG, "****************************************************************");


            // Chat_Service: ctx : ChannelHandlerContext(handler, [id: 0x1817f9fe, L:/192.168.1.64:56319 - R:/192.168.1.65:8000])
            // Chat_Service: msg : [you]{"Sender_Id":"3","Sender_Name":"인섭","Sender_Message":"dcccc","Sender_Profile":"1.jpg","Sender_Time":"2018년 01월 05일 금요일_100908"}

            //msg : [you]{"Sender_Id":"3","Sender_Name":"인섭","Sender_Message":"ggh","Sender_Profile":"1.jpg","Sender_Time":"2018년 01월 04일 목요일_163725"}
            //msg : [/192.168.1.64:55555] {"Sender_Id":"3","Sender_Name":"인섭","Sender_Message":"ggh","Sender_Profile":"1.jpg","Sender_Time":"2018년 01월 04일 목요일_163725"}


            Log.d(TAG, "****************************************************************");
            Log.d(TAG, "서비스 ChatClientHandler() : (받기) 1.서버에서 받은 메세지를 액티비티로 보내는 곳");
            Log.d(TAG, "서비스 Broad Cast Action : " + BROADCAST_NETTY_MESSAGE);
            Log.d(TAG, "**************************************************");

            Intent sendIntent = new Intent(BROADCAST_NETTY_MESSAGE);
            sendIntent.putExtra("MessageFromService", msg);
            sendBroadcast(sendIntent);

        }

        // 채팅 중 에러 발생시
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }

    } // Finish: ChatClientHandler
    // =========================================================================================================


    // =========================================================================================================
    // 메세지를 보내고 받아오는 걸 처리하는 곳
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "**************************************************");
        Log.d(TAG, "서비스 onStartCommand() : 3.액티비티로 부터 받은 데이터 처리");
        Log.d(TAG, "서비스 onStartCommand() : 근데 액티비티 단에서 서버로 바로 메세지를 보낼 수 있기 때문에 이 부분 필요 없을 듯");
        Log.d(TAG, "**************************************************");

        if (intent == null) {
            return Service.START_STICKY; // 이건 무슨 명령어인가?
        } else {
            processCommand(intent);
        }

        return super.onStartCommand(intent, flags, startId);

    }

    private void processCommand(Intent intent) {
        Log.d(TAG, "**************************************************");
        Log.d(TAG, "서비스 processCommand() : 4.액티비티에서 받아온 데이터를 실질적으로 처리하는 곳");
        Log.d(TAG, "**************************************************");

        // Log.d(TAG, "intent.getStringExtra(command) : " + intent.getStringExtra("command"));
        // String status = intent.getStringExtra(SERVICE_START);
    }
    // =========================================================================================================


    // =========================================================================================================
    // Service 객체와 Activity 사이에서 통신이 이루어질 때 사용하는 메소드
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemmented");
    }

    // 서비스가 종료될 때 실행되는 함수
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Chat_Service 서비스 종료");

    }

}

//package seopftware.fundmytravel.util.streaming;
//
//import android.util.Log;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelPipeline;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.DelimiterBasedFrameDecoder;
//import io.netty.handler.codec.Delimiters;
//import io.netty.handler.codec.string.StringDecoder;
//import io.netty.handler.codec.string.StringEncoder;
//
//
//public class ChatClient extends Thread {
//
//    private static final String TAG = "ChatClient";
//
//    private final String host;
//    private final int port;
//
//    Channel channel;
//    Bootstrap bootstrap;
//
//    public ChatClient(String host, int port) {
//        this.host=host;
//        this.port=port;
//    }
//
//    public void run() {
//        EventLoopGroup group = new NioEventLoopGroup();
//
//        try {
//
//            Log.d(TAG, "**************************************************");
//            Log.d(TAG, "1. 서버와 채널(소켓) 연결");
//            Log.d(TAG, "**************************************************");
//            // to set up a channel
//            bootstrap = new Bootstrap()
//                    .group(group)
//                    .channel(NioSocketChannel.class)
//                    .handler(new ChatClientInitializer());
//
//            // 서버에 최초 접속
//            channel = bootstrap.connect(host, port).sync().channel();
//            channel.writeAndFlush("서버와 연결되었습니다.");
//
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
//public class ChatClientInitializer extends ChannelInitializer<SocketChannel> {
//
//    static final int MESSAGE_SIZE = 8192;
//
//    @Override
//    protected void initChannel(SocketChannel ch) throws Exception {
//        // Here's define what Netty calls a pipeline
//        // It basically describes how we want to organize our communication
//
//        ChannelPipeline pipeline = ch.pipeline();
//
//        // First, tell netty we're expecting frames of at most 8192 in size, each delimited with line endings
//        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(MESSAGE_SIZE, Delimiters.lineDelimiter()));
//
//        // Since we're just exchanging Strings between the server and clients, we can use the StringDecoder to decode received bytes into Strings.
//        pipeline.addLast("decoder", new StringDecoder());
//
//        // StringEncoder to encode Strings into bytes, which we can then send over to the server
//        pipeline.addLast("encoder", new StringEncoder());
//
//        // finally, define a class which will handle all the decoded incoming Strings from the server
//        pipeline.addLast("handler", new ChatClientHandler());
//    }
//}

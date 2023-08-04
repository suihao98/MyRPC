package com.test.client.Impl;


import com.test.codec.KryoSerializer;
import com.test.codec.MyDecoder;
import com.test.codec.MyEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.NoArgsConstructor;

/**
 * 初始化，主要负责序列化的编码解码， 需要解决netty的粘包问题
 */
@NoArgsConstructor
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 这里使用自己编写的解码器
        pipeline.addLast(new MyDecoder());
        // 编码需要传入序列化器，这里是json，还支持ObjectSerializer，也可以自己实现其他的
        pipeline.addLast(new MyEncoder(new KryoSerializer()));
        pipeline.addLast(new NettyClientHandler());
    }
}

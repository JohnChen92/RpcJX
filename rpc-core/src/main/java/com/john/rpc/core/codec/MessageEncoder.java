package com.john.rpc.core.codec;

import com.john.rpc.core.meta.MsgBase;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<MsgBase> {

    private static final String DEFAULT_ENCODE = "utf-8";

    private static final int MAGIC_NUMBER = 0x0CAFFEE0;


    @Override
    protected void encode(ChannelHandlerContext ctx, MsgBase msg, ByteBuf byteBuf) throws Exception {
        ByteBufOutputStream writer = new ByteBufOutputStream(byteBuf);
        byte[] body = null;

        if (null != msg && null != msg.getBody() && "" != msg.getBody()) {
            body = msg.getBody().getBytes(DEFAULT_ENCODE);
        }

        writer.writeInt(MAGIC_NUMBER);

        writer.writeByte(1);

        writer.writeByte(msg.getType());

        writer.writeInt(msg.getSequence());

        if (null == body || 0 == body.length) {
            writer.writeInt(0);
        } else {
            writer.writeInt(body.length);
            writer.write(body);
        }

    }
}

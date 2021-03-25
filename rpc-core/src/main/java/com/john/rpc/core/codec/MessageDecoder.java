package com.john.rpc.core.codec;

import com.john.rpc.core.meta.MsgBase;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

    private static final int MAGIC_NUMBER = 0x0CAFFEE0;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        if (byteBuf.readableBytes() < 14) {
            return;
        }
        // 标记开始读取位置
        byteBuf.markReaderIndex();
        int magic_number = byteBuf.readInt();
        if (MAGIC_NUMBER != magic_number) {
            ctx.close();
            return;
        }

        @SuppressWarnings("unused")
        byte version = byteBuf.readByte();
        byte type = byteBuf.readByte();
        int squence = byteBuf.readInt();
        int length = byteBuf.readInt();

        if (length < 0) {
            ctx.close();
            return;
        }

        if (byteBuf.readableBytes() < length) {
            // 重置到开始读取位置
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] body = new byte[length];
        byteBuf.readBytes(body);
        MsgBase req = new MsgBase();
        req.setBody(new String(body, "utf-8"));
        req.setType(type);
        req.setSequence(squence);
        out.add(req);
    }
}

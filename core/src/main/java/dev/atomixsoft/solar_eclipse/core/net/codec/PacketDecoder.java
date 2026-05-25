package dev.atomixsoft.solar_eclipse.core.net.codec;

import dev.atomixsoft.solar_eclipse.core.net.packet.impl.ChatMessagePacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.impl.EntityMovePacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.impl.LoginPacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.impl.ShutdownPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class PacketDecoder  extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 4)
            return;

        int id = in.readInt();
        switch (id) {
            case 0 -> {
                if(in.readableBytes() < 12)
                    return;

                out.add(new ShutdownPacket(in.getBoolean(id)));
            }

            case 1 -> {
                if(in.readableBytes() < 12)
                    return;

                out.add(new LoginPacket(in.readString(512, StandardCharsets.UTF_8), in.readString(512, StandardCharsets.UTF_8)));
            }

            case 2 -> {
                if(in.readableBytes() < 12)
                    return;

                out.add(new EntityMovePacket(in.readInt(), in.readFloat(), in.readFloat()));
            }

            case 3 -> {
                if(in.readableBytes() < 12)
                    return;

                out.add(new ChatMessagePacket(in.readString(512, StandardCharsets.UTF_8), in.readString(512, StandardCharsets.UTF_8)));
            }
        }
    }

}

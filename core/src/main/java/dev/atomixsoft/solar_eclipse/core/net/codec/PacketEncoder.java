package dev.atomixsoft.solar_eclipse.core.net.codec;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import dev.atomixsoft.solar_eclipse.core.net.packet.PacketRegistry;
import dev.atomixsoft.solar_eclipse.core.net.packet.impl.EntityMovePacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.impl.ShutdownPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        int id = PacketRegistry.GetId(msg);
        out.writeInt(id);

        switch(msg) {
            case EntityMovePacket p -> {
                out.writeInt(p.id());
                out.writeFloat(p.x());
                out.writeFloat(p.y());
            }

            case ShutdownPacket p -> {
                out.writeBoolean(p.forced());
            }

            default -> throw new IllegalStateException("Unhandled packet encoder: " + msg.getClass());
        }
    }

}

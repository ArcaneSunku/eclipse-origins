package dev.atomixsoft.solar_eclipse.core.net.codec;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import dev.atomixsoft.solar_eclipse.core.net.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder  extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 4) return;

        in.markReaderIndex();
        int id = in.readInt();

        PacketCodec codec = PacketRegistry.GetCodec(id);
        if(codec == null)
            throw new IllegalStateException("Unknown packet id: " + id);

        try {
            Packet packet = codec.decode(in);
            out.add(packet);
        } catch (Exception e) {
            in.resetReaderIndex();
        }
    }

}

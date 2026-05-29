package dev.atomixsoft.solar_eclipse.core.net.codec;

import dev.atomixsoft.solar_eclipse.core.net.packet.impl.ChatMessagePacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.impl.EntityMovePacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.impl.LoginPacket;
import dev.atomixsoft.solar_eclipse.core.net.packet.impl.ShutdownPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
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
                if(in.readableBytes() < 1) {
                    in.resetReaderIndex();
                    return;
                }

                boolean forced = in.readBoolean();
                out.add(new ShutdownPacket(forced));
            }

            case 1 -> {
                String username = readString(in);
                String password = readString(in);

                if(username == null || password == null) {
                    in.resetReaderIndex();
                    return;
                }

                out.add(new LoginPacket(username, password));
            }

            case 2 -> {
                if(in.readableBytes() < 12) {
                    in.resetReaderIndex();
                    return;
                }

                int entityId = in.readInt();
                float x = in.readFloat();
                float y = in.readFloat();

                out.add(new EntityMovePacket(entityId, x, y));
            }

            case 3 -> {
                String sender = readString(in);
                String message = readString(in);

                if(sender == null || message == null) {
                    in.resetReaderIndex();
                    return;
                }

                out.add(new ChatMessagePacket(sender, message));
            }

            default -> throw new IllegalStateException("Unknown packet ID: " + id);
        }
    }

    private String readString(ByteBuf in) {
        if(in.readableBytes() < 4)
            return null;

        in.markReaderIndex();
        int length = in.readInt();

        if(in.readableBytes() < length) {
            in.resetReaderIndex();
            return null;
        }

        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }

}

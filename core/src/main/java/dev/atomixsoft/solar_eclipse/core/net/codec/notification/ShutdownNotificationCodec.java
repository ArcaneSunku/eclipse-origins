package dev.atomixsoft.solar_eclipse.core.net.codec.notification;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.ShutdownNotification;
import dev.atomixsoft.solar_eclipse.core.utils.CodecUtils;
import io.netty.buffer.ByteBuf;

public class ShutdownNotificationCodec implements PacketCodec<ShutdownNotification> {

    @Override
    public void encode(ShutdownNotification packet, ByteBuf out) {
        CodecUtils.writeString(out, packet.reason());
        out.writeInt(packet.shutdownTime());
    }

    @Override
    public ShutdownNotification decode(ByteBuf in) {
        String reason = CodecUtils.readString(in);
        int shutdownTime = in.readInt();

        return new ShutdownNotification(reason, shutdownTime);
    }

}

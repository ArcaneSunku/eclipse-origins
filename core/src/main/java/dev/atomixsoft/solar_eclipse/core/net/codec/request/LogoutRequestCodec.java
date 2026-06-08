package dev.atomixsoft.solar_eclipse.core.net.codec.request;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.LogoutRequest;
import io.netty.buffer.ByteBuf;

public class LogoutRequestCodec implements PacketCodec<LogoutRequest> {

    @Override
    public void encode(LogoutRequest packet, ByteBuf out) { }

    @Override
    public LogoutRequest decode(ByteBuf in) {
        return new LogoutRequest();
    }
}

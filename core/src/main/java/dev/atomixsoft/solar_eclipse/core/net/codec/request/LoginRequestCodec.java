package dev.atomixsoft.solar_eclipse.core.net.codec.request;

import dev.atomixsoft.solar_eclipse.core.utils.CodecUtils;
import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.LoginRequest;
import io.netty.buffer.ByteBuf;

public class LoginRequestCodec implements PacketCodec<LoginRequest> {

    @Override
    public void encode(LoginRequest packet, ByteBuf out) {
        CodecUtils.writeString(out, packet.username());
        CodecUtils.writeString(out, packet.password());
    }

    @Override
    public LoginRequest decode(ByteBuf in) {
        String username = CodecUtils.readString(in);
        String password = CodecUtils.readString(in);

        if (username == null || password == null)
            throw new IllegalStateException("Failed to decode LoginRequest");

        return new LoginRequest(username, password);
    }
}

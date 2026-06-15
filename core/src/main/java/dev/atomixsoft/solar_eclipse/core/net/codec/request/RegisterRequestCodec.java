package dev.atomixsoft.solar_eclipse.core.net.codec.request;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.RegisterRequest;
import dev.atomixsoft.solar_eclipse.core.utils.CodecUtils;
import io.netty.buffer.ByteBuf;

public class RegisterRequestCodec implements PacketCodec<RegisterRequest> {
    @Override
    public void encode(RegisterRequest packet, ByteBuf out) {
        CodecUtils.writeString(out, packet.username());
        CodecUtils.writeString(out, packet.password());
    }

    @Override
    public RegisterRequest decode(ByteBuf in) {
        String username = CodecUtils.readString(in);
        String password = CodecUtils.readString(in);

        return new RegisterRequest(username, password);
    }
}

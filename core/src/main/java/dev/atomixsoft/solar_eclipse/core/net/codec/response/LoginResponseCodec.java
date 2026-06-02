package dev.atomixsoft.solar_eclipse.core.net.codec.response;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.LoginResponse;
import dev.atomixsoft.solar_eclipse.core.utils.CodecUtils;
import io.netty.buffer.ByteBuf;

public class LoginResponseCodec implements PacketCodec<LoginResponse> {

    @Override
    public void encode(LoginResponse packet, ByteBuf out) {
        out.writeBoolean(packet.success());
        CodecUtils.writeString(out, packet.message());
        out.writeInt(packet.playerEntityId());
        out.writeInt(packet.mapId());
    }

    @Override
    public LoginResponse decode(ByteBuf in) {
        boolean success = in.readBoolean();
        String msg = CodecUtils.readString(in);
        int playerId = in.readInt();
        int mapId = in.readInt();

        return new LoginResponse(success, msg, playerId, mapId);
    }

}

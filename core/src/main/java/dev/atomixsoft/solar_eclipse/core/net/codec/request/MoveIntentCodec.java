package dev.atomixsoft.solar_eclipse.core.net.codec.request;

import dev.atomixsoft.solar_eclipse.core.game.character.Direction;
import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.MoveIntent;
import io.netty.buffer.ByteBuf;

public class MoveIntentCodec implements PacketCodec<MoveIntent> {
    @Override
    public void encode(MoveIntent packet, ByteBuf out) {
        out.writeInt(packet.dx());
        out.writeInt(packet.dy());
        out.writeInt(packet.sequence());
    }

    @Override
    public MoveIntent decode(ByteBuf in) {
        int dx = in.readInt();
        int dy = in.readInt();
        int sequence = in.readInt();

        return new MoveIntent(dx, dy, sequence);
    }
}

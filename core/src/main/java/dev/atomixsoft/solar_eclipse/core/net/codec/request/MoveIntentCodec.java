package dev.atomixsoft.solar_eclipse.core.net.codec.request;

import dev.atomixsoft.solar_eclipse.core.game.character.Direction;
import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.MoveIntent;
import io.netty.buffer.ByteBuf;

public class MoveIntentCodec implements PacketCodec<MoveIntent> {
    @Override
    public void encode(MoveIntent packet, ByteBuf out) {
        out.writeInt(packet.entityId());
        out.writeByte(packet.direction().asByte());
        out.writeInt(packet.sequence());
    }

    @Override
    public MoveIntent decode(ByteBuf in) {
        int id = in.readInt();
        Direction dir = Direction.Get(in.readByte());
        int sequence = in.readInt();

        return new MoveIntent(id, dir, sequence);
    }
}

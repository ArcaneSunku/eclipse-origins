package dev.atomixsoft.solar_eclipse.core.net.codec.request;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.request.InventoryMoveRequest;
import io.netty.buffer.ByteBuf;

public class InventoryMoveRequestCodec implements PacketCodec<InventoryMoveRequest> {
    @Override
    public void encode(InventoryMoveRequest packet, ByteBuf out) {
        out.writeInt(packet.fromSlot());
        out.writeInt(packet.toSlot());
    }

    @Override
    public InventoryMoveRequest decode(ByteBuf in) {
        int fromSlot = in.readInt();
        int toSlot = in.readInt();

        return new InventoryMoveRequest(fromSlot, toSlot);
    }
}

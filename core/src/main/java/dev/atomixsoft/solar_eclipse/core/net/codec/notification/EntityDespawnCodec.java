package dev.atomixsoft.solar_eclipse.core.net.codec.notification;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.EntityDespawn;
import io.netty.buffer.ByteBuf;

public class EntityDespawnCodec implements PacketCodec<EntityDespawn> {
    @Override
    public void encode(EntityDespawn packet, ByteBuf out) {
        out.writeInt(packet.entityId());
    }

    @Override
    public EntityDespawn decode(ByteBuf in) {
        int entityId = in.readInt();

        return new EntityDespawn(entityId);
    }
}

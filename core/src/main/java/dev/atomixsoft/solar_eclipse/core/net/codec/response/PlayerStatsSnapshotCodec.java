package dev.atomixsoft.solar_eclipse.core.net.codec.response;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.packet.response.PlayerStatsSnapshot;
import io.netty.buffer.ByteBuf;

public class PlayerStatsSnapshotCodec implements PacketCodec<PlayerStatsSnapshot> {
    @Override
    public void encode(PlayerStatsSnapshot packet, ByteBuf out) {
        out.writeInt(packet.level());
        out.writeInt(packet.health());
        out.writeInt(packet.maxHealth());
        out.writeInt(packet.spirit());
        out.writeInt(packet.maxSpirit());
        out.writeInt(packet.experience());
        out.writeInt(packet.maxExperience());
        out.writeInt(packet.gold());
    }

    @Override
    public PlayerStatsSnapshot decode(ByteBuf in) {
        int level = in.readInt();
        int health = in.readInt();
        int maxHealth = in.readInt();
        int spirit = in.readInt();
        int maxSpirit = in.readInt();
        int experience = in.readInt();
        int maxExperience = in.readInt();
        int gold = in.readInt();

        return new PlayerStatsSnapshot(level, health, maxHealth, spirit, maxSpirit, experience, maxExperience, gold);
    }
}

package dev.atomixsoft.solar_eclipse.core.net.codec;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import io.netty.buffer.ByteBuf;

public interface PacketCodec<T extends Packet> {
    void encode(T packet, ByteBuf out);
    T decode(ByteBuf in);
}

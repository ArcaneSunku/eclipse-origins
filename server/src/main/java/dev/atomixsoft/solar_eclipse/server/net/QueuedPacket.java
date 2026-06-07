package dev.atomixsoft.solar_eclipse.server.net;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;
import io.netty.channel.Channel;

public record QueuedPacket(Channel channel, Packet packet) {
}

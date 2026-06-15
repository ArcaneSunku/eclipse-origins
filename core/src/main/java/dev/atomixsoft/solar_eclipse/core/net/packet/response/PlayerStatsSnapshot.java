package dev.atomixsoft.solar_eclipse.core.net.packet.response;

import dev.atomixsoft.solar_eclipse.core.net.packet.Packet;

public record PlayerStatsSnapshot(int level, int health, int maxHealth,
                                  int spirit, int maxSpirit,
                                  int experience, int maxExperience, int gold) implements Packet {
}

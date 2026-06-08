package dev.atomixsoft.solar_eclipse.server.net.services.records;

import dev.atomixsoft.solar_eclipse.core.game.character.CharacterData;

public record LoginResultRec(boolean success, String message, int accountId, int characterId, int mapId, CharacterData data) {
}

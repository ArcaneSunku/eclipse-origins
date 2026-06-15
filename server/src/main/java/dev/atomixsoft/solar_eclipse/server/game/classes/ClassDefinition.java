package dev.atomixsoft.solar_eclipse.server.game.classes;

import java.util.List;

public record ClassDefinition(int id, String name,
                              List<Integer> maleSprites, List<Integer> femaleSprites,
                              int strength, int endurance, int intelligence, int agility, int willpower,
                              List<StartingItem> startingItems, List<Integer> startingSpells) {
}

package dev.atomixsoft.solar_eclipse.server.net.services;

import dev.atomixsoft.solar_eclipse.core.config.INIConfigurationFile;
import dev.atomixsoft.solar_eclipse.server.game.classes.ClassDefinition;
import dev.atomixsoft.solar_eclipse.server.game.classes.StartingItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassService {

    private final Map<Integer, ClassDefinition> m_Classes;

    public ClassService() {
        m_Classes = new HashMap<>();
    }

    public void load(String path) {
        try {
            INIConfigurationFile config = new INIConfigurationFile();
            config.load(path);

            int maxClasses = parseInt(config.getValue("INIT.MaxClasses"), 0);

            for(int id = 1; id <= maxClasses; id++) {
                String section = "CLASS" + id + ".";

                String name = config.getValue(section + "Name");

                List<Integer> maleSprites = parseList(config.getValue(section + "MaleSprite"));
                List<Integer> femaleSprites = parseList(config.getValue(section + "FemaleSprite"));

                int str = parseInt(config.getValue(section + "Strength"), 1);
                int end = parseInt(config.getValue(section + "Endurance"), 1);
                int intel =  parseInt(config.getValue(section + "Intelligence"), 1);
                int agi =  parseInt(config.getValue(section + "Agility"), 1);
                int will =   parseInt(config.getValue(section + "Willpower"), 1);

                List<StartingItem> startingItems = readStartingItems(config, section);
                List<Integer> startingSpells = readStartingSpells(config, section);

                ClassDefinition definition = new ClassDefinition(
                        id, name,
                        maleSprites,
                        femaleSprites,
                        str, end,
                        intel, agi,
                        will, startingItems, startingSpells
                );

                m_Classes.put(id, definition);
            }

            System.out.println("Loaded " + m_Classes.size() + " classes.");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load class data from: " + path, e);
        }
    }

    public ClassDefinition get(int id) {
        return m_Classes.get(id);
    }

    public Map<Integer, ClassDefinition> all() {
        return m_Classes;
    }

    private List<StartingItem> readStartingItems(INIConfigurationFile config, String section) {
        List<StartingItem> items = new ArrayList<>();

        int count = parseInt(config.getValue(section + "StartItemCount"), 0);

        for(int i = 1; i <= count; i++) {
            int itemId = parseInt(config.getValue(section + "StartItem" + i), 0);
            int amount = parseInt(config.getValue(section + "StartValue" + i), 0);

            if(itemId > 0 && amount > 0)
                items.add(new StartingItem(itemId, amount));
        }

        return items;
    }

    private List<Integer> readStartingSpells(INIConfigurationFile config, String section) {
        List<Integer> spells = new ArrayList<>();

        int count = parseInt(config.getValue(section + "StartSpellCount"), 0);

        for(int i = 1; i <= count; i++) {
            int spellId = parseInt(config.getValue(section + "StartSpell" + i), 0);

            if(spellId > 0)
                spells.add(spellId);
        }

        return spells;
    }

    private List<Integer> parseList(String value) {
        List<Integer> result = new ArrayList<>();

        if(value == null || value.isBlank())
            return result;

        for(String part : value.split(","))
            result.add(parseInt(part.trim(), 0));

        return result;
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch(Exception e) {
            return fallback;
        }
    }

}

package dev.atomixsoft.solar_eclipse.server.net.services;

import dev.atomixsoft.solar_eclipse.core.config.INIConfigurationFile;
import dev.atomixsoft.solar_eclipse.core.net.data.ItemDefinitionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemService {

    private final Map<Integer, ItemDefinitionData> m_Items;

    public ItemService() {
        m_Items = new HashMap<>();
    }

    public void load(String path) {
        try {
            INIConfigurationFile config = new INIConfigurationFile();
            config.load(path);

            int maxItems = parseInt(config.getValue("INIT.MaxItems"), 0);

            for(int id = 1; id <= maxItems; id++) {
                String section = "ITEM" + id + ".";

                ItemDefinitionData item = new ItemDefinitionData(id,
                        config.getValue(section + "Name"),
                        config.getValue(section + "Description"),
                        parseInt(config.getValue(section + "Icon"), 0),
                        parseInt(config.getValue(section + "MaxStack"), 1));

                m_Items.put(id, item);
            }

            System.out.println("Loaded " + m_Items.size() + " items.");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load item data from: " + path, e);
        }
    }

    public List<ItemDefinitionData> createSnapshot() {
        List<ItemDefinitionData> items = new ArrayList<>();

        for(var item : m_Items.values()) {
            items.add(new ItemDefinitionData(
                    item.id(),
                    item.name(),
                    item.description(),
                    item.iconId(),
                    item.maxStack()
            ));
        }

        return items;
    }

    public ItemDefinitionData get(int id) {
        return m_Items.get(id);
    }

    public boolean exists(int id) {
        return m_Items.containsKey(id);
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return fallback;
        }
    }

}

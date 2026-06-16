package dev.atomixsoft.solar_eclipse.client.game;

import dev.atomixsoft.solar_eclipse.core.game.Item;
import dev.atomixsoft.solar_eclipse.core.net.data.ItemDefinitionData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientItemDefinitions {

    private final Map<Integer, ItemDefinitionData> m_Items;

    public ClientItemDefinitions() {
        m_Items = new HashMap<>();
    }

    public void applySnapshot(List<ItemDefinitionData> items) {
        m_Items.clear();

        for(ItemDefinitionData item : items)
            m_Items.put(item.id(), item);
    }

    public ItemDefinitionData get(int id) {
        return m_Items.get(id);
    }

    public boolean exists(int id) {
        return m_Items.containsKey(id);
    }

}

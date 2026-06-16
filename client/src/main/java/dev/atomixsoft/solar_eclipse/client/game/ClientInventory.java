package dev.atomixsoft.solar_eclipse.client.game;

import dev.atomixsoft.solar_eclipse.core.net.data.InventorySlotData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientInventory {

    private final Map<Integer, InventorySlotData> m_Slots;

    public ClientInventory() {
        m_Slots = new HashMap<>();
    }

    public void applySnapshot(List<InventorySlotData> slots) {
        m_Slots.clear();

        for(InventorySlotData slot : slots)
            m_Slots.put(slot.slot(), slot);
    }

    public InventorySlotData getSlot(int slot) {
        return m_Slots.get(slot);
    }

    public Map<Integer, InventorySlotData> slots() {
        return m_Slots;
    }

}

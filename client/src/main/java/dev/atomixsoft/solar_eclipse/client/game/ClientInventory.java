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

    public void swapSlots(int fromSlot, int toSlot) {
        InventorySlotData from = m_Slots.get(fromSlot);
        InventorySlotData to = m_Slots.get(toSlot);

        if(from == null || from.itemId() <= 0)
            return;

        m_Slots.put(toSlot, new InventorySlotData(toSlot, from.itemId(), from.amount()));

        if(to != null && to.itemId() > 0)
            m_Slots.put(fromSlot, new InventorySlotData(from.slot(), to.itemId(), to.amount()));
        else
            m_Slots.put(fromSlot, new InventorySlotData(from.slot(), 0, 0));
    }

    public InventorySlotData getSlot(int slot) {
        return m_Slots.get(slot);
    }

    public Map<Integer, InventorySlotData> slots() {
        return m_Slots;
    }

}

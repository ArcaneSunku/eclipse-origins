package dev.atomixsoft.solar_eclipse.server.net.services;

import dev.atomixsoft.solar_eclipse.core.game.Constants;
import dev.atomixsoft.solar_eclipse.core.net.data.InventorySlotData;
import dev.atomixsoft.solar_eclipse.server.database.records.InventorySlotRecord;
import dev.atomixsoft.solar_eclipse.server.database.repositories.InventoryRepository;
import dev.atomixsoft.solar_eclipse.server.game.classes.StartingItem;

import java.util.ArrayList;
import java.util.List;

public class InventoryService {

    private final InventoryRepository m_InventoryRepository;
    private final ItemService m_ItemService;

    public InventoryService(InventoryRepository inventoryRepository, ItemService itemService) {
        m_InventoryRepository = inventoryRepository;
        m_ItemService = itemService;
    }

    public void createStartingInventory(int characterId, List<StartingItem> startingItems) {
        m_InventoryRepository.createEmptyInventory(characterId);

        int slot = 0;

        for(StartingItem item : startingItems) {
            if(slot >= Constants.MAX_INV)
                break;

            if(!m_ItemService.exists(item.itemId()))
                continue;

            m_InventoryRepository.setSlot(characterId, slot, item.itemId(), item.amount());
            slot++;
        }
    }

    public List<InventorySlotData> createSnapshot(int characterId) {
        List<InventorySlotRecord> records = m_InventoryRepository.findByCharacterId(characterId);

        List<InventorySlotData> slots = new ArrayList<>();
        for(InventorySlotRecord record : records) {
            slots.add(new InventorySlotData(
                    record.slot(),
                    record.itemId(),
                    record.amount()
            ));
        }

        return slots;
    }

    public List<InventorySlotRecord> loadInventory(int characterId) {
        return m_InventoryRepository.findByCharacterId(characterId);
    }

}

package dev.atomixsoft.solar_eclipse.server.database.repositories;

import dev.atomixsoft.solar_eclipse.core.game.Constants;
import dev.atomixsoft.solar_eclipse.server.database.Database;
import dev.atomixsoft.solar_eclipse.server.database.records.InventorySlotRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepository {

    private final Database m_Database;

    public InventoryRepository(Database database) {
        m_Database = database;
    }

    public void createEmptyInventory(int characterId) {
        String sql = """
            INSERT OR IGNORE INTO character_inventory (
                character_id, slot, item_id, amount
            )
            VALUES (?, ?, 0, 0)
        """;

        try(Connection connection = m_Database.connect();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            for(int slot = 0; slot < Constants.MAX_INV; slot++) {
                statement.setInt(1, characterId);
                statement.setInt(2, slot);
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create inventory for character: " + characterId, e);
        }
    }

    public void swapSlots(int characterId, int fromSlot, int toSlot) {
        InventorySlotRecord from = findSlot(characterId, fromSlot);
        InventorySlotRecord to = findSlot(characterId, toSlot);

        if(from == null || from.itemId() <= 0)
            return;

        setSlot(characterId, toSlot, from.itemId(), from.amount());

        if(to != null & to.itemId() > 0)
            setSlot(characterId, fromSlot, to.itemId(), to.amount());
        else
            setSlot(characterId, fromSlot, 0, 0);
    }

    public InventorySlotRecord findSlot(int characterId, int slot) {
        String sql = """
                SELECT character_id, slot, item_id, amount
                FROM character_inventory
                WHERE character_id = ? AND slot = ?
                """;

        try(Connection connection = m_Database.connect();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, characterId);
            statement.setInt(2, slot);

            try(ResultSet result = statement.executeQuery()) {
                if(!result.next())
                    return null;

                return new InventorySlotRecord(result.getInt("character_id"), result.getInt("slot"),
                        result.getInt("item_id"), result.getInt("amount"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find inventory slot: " + slot, e);
        }
    }

    public List<InventorySlotRecord> findByCharacterId(int characterId) {
        String sql = """
            SELECT character_id, slot, item_id, amount
            FROM character_inventory
            WHERE character_id = ?
            ORDER BY slot;
        """;

        List<InventorySlotRecord> slots = new ArrayList<>();

        try(Connection connection = m_Database.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, characterId);

            try(ResultSet result = statement.executeQuery()) {
                while(result.next()) {
                    slots.add(new InventorySlotRecord(
                            result.getInt("character_id"),
                            result.getInt("slot"),
                            result.getInt("item_id"),
                            result.getInt("amount")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load inventory for character: " + characterId, e);
        }

        return slots;
    }

    public void setSlot(int characterId, int slot, int itemId, int amount) {
        String sql = """
            UPDATE character_inventory
            SET item_id = ?, amount = ?
            WHERE character_id = ? AND slot = ?;
        """;

        try(Connection connection = m_Database.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, itemId);
            statement.setInt(2, amount);
            statement.setInt(3, characterId);
            statement.setInt(4, slot);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update inventory slot: " + slot, e);
        }
    }

}

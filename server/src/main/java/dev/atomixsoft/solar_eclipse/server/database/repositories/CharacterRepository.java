package dev.atomixsoft.solar_eclipse.server.database.repositories;

import dev.atomixsoft.solar_eclipse.core.game.Constants;
import dev.atomixsoft.solar_eclipse.core.game.character.CharacterData;
import dev.atomixsoft.solar_eclipse.server.database.Database;
import dev.atomixsoft.solar_eclipse.server.database.records.CharacterRecord;

import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CharacterRepository {

    private final Database m_Database;

    public CharacterRepository(Database database) {
        m_Database = database;
    }

    public CharacterRecord findByAccountAndSlot(int accountId, int slot) {
        String sql = """
                SELECT id, account_id, slot, name, texture_id, sex, map_id, x, y, gold, created_at
                FROM characters
                WHERE account_id = ? AND slot = ?;
                """;

        try (Connection connection = m_Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountId);
            statement.setInt(2, slot);

            try(ResultSet result =  statement.executeQuery()) {
                if(!result.next())
                    return null;

                return readCharacter(result);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find character for account: " + accountId, e);
        }
    }

    public CharacterRecord createDefault(int accountId, String username) {
        String sql = """
                INSERT INTO characters (
                    account_id, slot, name, texture_id, sex, map_id, x, y, gold, created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        int slot = 0;
        String name = username;
        int textureId = 1;
        byte sex = Constants.SEX_MALE;
        int mapId = 0;
        int x = 2;
        int y = 3;
        int gold = 0;
        long now = System.currentTimeMillis();

        try (Connection connection = m_Database.connect();
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, accountId);
            statement.setInt(2, slot);
            statement.setString(3, name);
            statement.setInt(4, textureId);
            statement.setByte(5, sex);
            statement.setInt(6, mapId);
            statement.setInt(7, x);
            statement.setInt(8, y);
            statement.setInt(9, gold);
            statement.setLong(10, now);

            statement.execute();

            try(ResultSet keys = statement.getGeneratedKeys()) {
                if(!keys.next())
                    throw new IllegalStateException("Failed to retrieve created character id.");

                return new CharacterRecord(keys.getInt(1), accountId, slot,
                        name, textureId, sex, mapId, x, y, gold, now);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create default character for account: " + accountId, e);
        }
    }

    public void saveState(int characterId, int mapId, int x, int y, int gold) {
        String sql = """
                UPDATE characters
                SET map_id = ?, x = ?, y = ?, gold = ?
                WHERE id = ?
                """;

        try(Connection connection = m_Database.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, mapId);
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, gold);
            statement.setInt(5, characterId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save character state for character: " + characterId, e);
        }
    }

    public CharacterRecord findOrCreateDefault(int accountId, String username) {
        CharacterRecord existing = findByAccountAndSlot(accountId, 0);
        if (existing != null)
            return existing;

        return createDefault(accountId, username);
    }

    public CharacterData toCharacterData(CharacterRecord record) {
        CharacterData data = new CharacterData();

        data.name = record.name();
        data.accountId = record.accountId();
        data.characterId = record.id();
        data.mapId = record.mapId();
        data.textureId = record.textureId();
        data.sex = record.sex();
        data.x = record.x();
        data.y =  record.y();
        data.player = true;

        return data;
    }

    private CharacterRecord readCharacter(ResultSet result) throws SQLException {
        return new CharacterRecord(result.getInt("id"), result.getInt("account_id"), result.getInt("slot"),
                result.getString("name"), result.getInt("texture_id"),
                result.getByte("sex"), result.getInt("map_id"),
                result.getInt("x"), result.getInt("y"), result.getInt("gold"),
                result.getInt("created_at"));
    }

}

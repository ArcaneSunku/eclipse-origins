package dev.atomixsoft.solar_eclipse.server.database.repositories;

import dev.atomixsoft.solar_eclipse.core.game.Constants;
import dev.atomixsoft.solar_eclipse.core.game.character.CharacterData;
import dev.atomixsoft.solar_eclipse.server.database.Database;
import dev.atomixsoft.solar_eclipse.server.database.records.CharacterRecord;
import dev.atomixsoft.solar_eclipse.server.database.records.CharacterSummary;
import dev.atomixsoft.solar_eclipse.server.game.classes.ClassDefinition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CharacterRepository {

    private final Database m_Database;

    public CharacterRepository(Database database) {
        m_Database = database;
    }

    public CharacterRecord findByAccountAndSlot(int accountId, int slot) {
        String sql = """
                SELECT id, account_id, slot, name, texture_id, sex, map_id, x, y, level,
                       health, max_health, spirit, max_spirit, experience, max_experience, 
                       gold, created_at
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
        throw new UnsupportedOperationException("Default character creation now request a class.");
    }

    public void saveState(int characterId, int mapId, int x, int y, int level,
                          int health, int maxHealth, int spirit, int maxSpirit, int experience, int maxExperience, int gold) {
        String sql = """
                UPDATE characters
                SET map_id = ?, x = ?, y = ?, level = ?,
                    health = ?, max_health = ?,
                    spirit = ?, max_spirit = ?,
                    experience = ?, max_experience = ?,
                    gold = ?
                WHERE id = ?
                """;

        try(Connection connection = m_Database.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, mapId);
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, level);
            statement.setInt(5, health);
            statement.setInt(6, maxHealth);
            statement.setInt(7, spirit);
            statement.setInt(8, maxSpirit);
            statement.setInt(9, experience);
            statement.setInt(10, maxExperience);
            statement.setInt(11, gold);
            statement.setInt(12, characterId);

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
        data.level = record.level();
        data.health = record.health();
        data.maxHealth = record.maxHealth();
        data.spirit = record.spirit();
        data.maxSpirit = record.maxSpirit();
        data.experience = record.experience();
        data.maxExperience = record.maxExperience();
        data.gold = record.gold();
        data.player = true;

        return data;
    }

    public CharacterRecord createCharacter(int accountId, int slot, String name, ClassDefinition classDef, byte sex, int spriteId) {
        String sql = """
                INSERT INTO characters (
                    account_id, slot, name, texture_id, sex, map_id, x, y, level,
                    health, max_health, spirit, max_spirit, experience, max_experience,
                    gold, created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        int mapId = 0;
        int x = 2;
        int y = 3;

        int level = 1;

        int maxHealth = 50 + classDef.endurance() * 10;
        int health = maxHealth;

        int maxSpirit = 30 + classDef.willpower() * 5 + classDef.intelligence() * 5;
        int spirit = maxSpirit;

        int experience = 0;
        int maxExperience = 100;

        int gold = 0;
        long now = System.currentTimeMillis();

        try (Connection connection = m_Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, accountId);
            statement.setInt(2, slot);
            statement.setString(3, name);
            statement.setInt(4, spriteId);
            statement.setByte(5, sex);
            statement.setInt(6, mapId);
            statement.setInt(7, x);
            statement.setInt(8, y);
            statement.setInt(9, level);
            statement.setInt(10, health);
            statement.setInt(11, maxHealth);
            statement.setInt(12, spirit);
            statement.setInt(13, maxSpirit);
            statement.setInt(14, experience);
            statement.setInt(15, maxExperience);
            statement.setInt(16, gold);
            statement.setLong(17, now);

            statement.executeUpdate();

            try(ResultSet keys = statement.getGeneratedKeys()) {
                if(!keys.next())
                    throw new IllegalStateException("Failed to retrieve created character id.");

                return new CharacterRecord(keys.getInt(1), accountId, slot,
                        name, spriteId, sex, mapId, x, y, level,
                        health, maxHealth, spirit, maxSpirit, experience, maxExperience,
                        gold, now);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create default character for account: " + accountId, e);
        }
    }

    public List<CharacterSummary> getCharacterSummaries(int accountId) {
        String sql = """
                SELECT slot, name, level, texture_id
                FROM characters
                WHERE account_id = ?
                ORDER BY slot
                """;

        List<CharacterSummary> characters = new ArrayList<>();
        try(Connection connection = m_Database.connect();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountId);

            try(ResultSet result = statement.executeQuery()) {
                while(result.next()) {
                    characters.add(new CharacterSummary(result.getInt("slot"),
                            result.getString("name"),
                            result.getInt("level"),
                            result.getInt("texture_id")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load character list for account: " + accountId, e);
        }

        return characters;
    }

    private CharacterRecord readCharacter(ResultSet result) throws SQLException {
        return new CharacterRecord(result.getInt("id"), result.getInt("account_id"), result.getInt("slot"),
                result.getString("name"), result.getInt("texture_id"),
                result.getByte("sex"), result.getInt("map_id"),
                result.getInt("x"), result.getInt("y"), result.getInt("level"),
                result.getInt("health"), result.getInt("max_health"),
                result.getInt("spirit"), result.getInt("max_spirit"),
                result.getInt("experience"), result.getInt("max_experience"),
                result.getInt("gold"), result.getInt("created_at"));
    }

}

package dev.atomixsoft.solar_eclipse.server.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMigrations {

    private DatabaseMigrations() {}

    public static void migrate(Connection connection) throws SQLException {
        try(Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE COLLATE NOCASE,
                    password_hash TEXT NOT NULL,
                    created_at INTEGER NOT NULL
                );
            """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS characters (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    account_id INTEGER NOT NULL,
                    slot INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    texture_id INTEGER NOT NULL,
                    sex INTEGER NOT NULL,
                    map_id INTEGER NOT NULL,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    created_at INTEGER NOT NULL,
                    
                    UNIQUE(account_id, slot),
                    FOREIGN KEY(account_id) REFERENCES accounts(id) ON DELETE CASCADE
                );
            """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS character_inventory (
                    character_id INTEGER NOT NULL,
                    slot INTEGER NOT NULL,
                    item_id INTEGER NOT NULL DEFAULT 0,
                    amount INTEGER NOT NULL DEFAULT 0,
                    
                    PRIMARY KEY(character_id, slot),
                    FOREIGN KEY(character_id) REFERENCES characters(id) ON DELETE CASCADE
                );
            """);

            addColumnIfMissing(connection, "characters", "level", "INTEGER NOT NULL DEFAULT 1");
            addColumnIfMissing(connection, "characters", "health", "INTEGER NOT NULL DEFAULT 100");
            addColumnIfMissing(connection, "characters", "max_health", "INTEGER NOT NULL DEFAULT 100");
            addColumnIfMissing(connection, "characters", "spirit", "INTEGER NOT NULL DEFAULT 50");
            addColumnIfMissing(connection, "characters", "max_spirit", "INTEGER NOT NULL DEFAULT 50");
            addColumnIfMissing(connection, "characters", "experience", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfMissing(connection, "characters", "max_experience", "INTEGER NOT NULL DEFAULT 100");
            addColumnIfMissing(connection, "characters", "gold", "INTEGER NOT NULL DEFAULT 0");
        }
    }

    private static void addColumnIfMissing(Connection connection, String table, String column, String definition) throws SQLException {
        try(Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition + ";");
        } catch (SQLException e) {
            if(!e.getMessage().contains("duplicate column name")) {
                throw e;
            }
        }
    }

}

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
                    password TEXT NOT NULL,
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
                    gold INTEGER NOT NULL DEFAULT 0,
                    created_at INTEGER NOT NULL,
                    
                    UNIQUE(account_id, slot),
                    FOREIGN KEY(account_id) REFERENCES accounts(id) ON DELETE CASCADE
                );
            """);
        }
    }

}

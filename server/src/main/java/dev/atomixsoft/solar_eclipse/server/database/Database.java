package dev.atomixsoft.solar_eclipse.server.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private final Path m_DatabasePath;

    public Database(Path databasePath) {
        m_DatabasePath = databasePath;
    }

    public void initialize() {
        try {
            Files.createDirectories(m_DatabasePath.getParent());

            try(Connection connection = connect(); Statement statement = connection.createStatement()) {
                DatabaseMigrations.migrate(connection);
                enableWal(connection);
            }
        } catch(IOException | SQLException e) {
            throw new IllegalStateException("Failed to initialize database: " + m_DatabasePath, e);
        }
    }

    private void enableWal(Connection connection) throws SQLException {
        try(Statement statement = connection.createStatement()) {
            statement.executeQuery("PRAGMA journal_mode = WAL;");
        }
    }

    public Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + m_DatabasePath);

        try(Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON;");
        }

        return connection;
    }

}

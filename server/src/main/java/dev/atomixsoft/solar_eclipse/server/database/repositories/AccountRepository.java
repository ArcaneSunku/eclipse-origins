package dev.atomixsoft.solar_eclipse.server.database.repositories;

import dev.atomixsoft.solar_eclipse.server.database.Database;
import dev.atomixsoft.solar_eclipse.server.database.records.AccountRecord;

import java.sql.*;

public class AccountRepository {

    private final Database m_Database;

    public AccountRepository(Database database) {
        m_Database = database;
    }

    public AccountRecord findByUsername(String username) {
        String sql = """
                SELECT id, username, password_hash, created_at
                FROM accounts
                WHERE username = ?
                """;

        try (Connection connection = m_Database.connect()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);

            try(ResultSet result = statement.executeQuery()) {
                if(!result.next())
                    return null;

                return readAccount(result);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find account: " + username, e);
        }
    }

    public AccountRecord create(String username, String passwordHash) {
        String sql = """
                INSERT INTO accounts (username, password_hash, created_at)
                VALUES (?, ?, ?)
                """;

        long now = System.currentTimeMillis();

        try (Connection connection = m_Database.connect();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setLong(3, now);

            statement.executeUpdate();

            try(ResultSet keys = statement.getGeneratedKeys()) {
                if(!keys.next())
                    throw new IllegalStateException("Failed to retrieve created account id.");

                return new AccountRecord(keys.getInt(1), username, passwordHash, now);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create account: " + username, e);
        }
    }

    public AccountRecord findOrCreate(String username, String password) {
        AccountRecord existing = findByUsername(username);

        if(existing != null)
            return existing;

        return create(username, password);
    }

    private AccountRecord readAccount(ResultSet result) throws SQLException {
        return new AccountRecord(result.getInt("id"),
                result.getString("username"),
                result.getString("password_hash"),
                result.getLong("created_at"));
    }

}

package dev.atomixsoft.solar_eclipse.server.net.services;

import dev.atomixsoft.solar_eclipse.server.database.records.AccountRecord;
import dev.atomixsoft.solar_eclipse.server.database.repositories.AccountRepository;
import dev.atomixsoft.solar_eclipse.server.net.services.records.LoginResultRec;
import dev.atomixsoft.solar_eclipse.server.security.PasswordHasher;

public class AuthenticationService {

    private final AccountRepository m_Accounts;

    public AuthenticationService(AccountRepository accounts) {
        m_Accounts = accounts;
    }

    public LoginResultRec login(String username, String password) {
        AccountRecord account = m_Accounts.findByUsername(username);

        if(account == null)
            return new LoginResultRec(false, "Account does not exist.", -1, "");

        if(!PasswordHasher.verify(password, account.passwordHash()))
            return new LoginResultRec(false, "Invalid password.", account.id(), "");

        return new LoginResultRec(true, "Welcome " + account.username(), account.id(), account.username());
    }

    public LoginResultRec register(String username, String password) {
        if(m_Accounts.findByUsername(username.trim()) != null)
            return new LoginResultRec(false, "Account already exists.", -1, "");

        String hash = PasswordHasher.hash(password);
        AccountRecord account = m_Accounts.create(username.trim(), hash);

        return new LoginResultRec(true, "Registered " + account.username(), account.id(), account.username());
    }

}

package dev.atomixsoft.solar_eclipse.server.net.services;

import dev.atomixsoft.solar_eclipse.server.database.records.AccountRecord;
import dev.atomixsoft.solar_eclipse.server.database.records.CharacterRecord;
import dev.atomixsoft.solar_eclipse.server.database.repositories.AccountRepository;
import dev.atomixsoft.solar_eclipse.server.database.repositories.CharacterRepository;
import dev.atomixsoft.solar_eclipse.server.net.services.records.LoginResultRec;

public class AuthenticationService {

    private final AccountRepository m_Accounts;
    private final CharacterRepository m_Characters;

    public AuthenticationService(AccountRepository accounts, CharacterRepository characters) {
        m_Accounts = accounts;
        m_Characters = characters;
    }

    public LoginResultRec login(String username, String password) {
        if(username == null || username.isEmpty())
            return new LoginResultRec(false, "Username cannot be empty.", -1, -1, -1, null);

        if(password == null)
            password = "";

        AccountRecord account = m_Accounts.findOrCreate(username, password);
        if(!account.password().equals(password))
            return new LoginResultRec(false, "Password does not match.", -1, -1, -1, null);

        CharacterRecord character = m_Characters.findOrCreateDefault(account.id(), account.username());
        return new LoginResultRec(true, "Welcome " + account.username(), account.id(), character.id(), character.mapId(), m_Characters.toCharacterData(character));
    }

    public boolean validate(String username, String password) {
        // TODO: Database Validation
        return true;
    }

}

package dev.atomixsoft.solar_eclipse.core.game;

import dev.atomixsoft.solar_eclipse.core.game.character.CharacterData;
import dev.atomixsoft.solar_eclipse.core.game.character.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Account {

    public final List<CharacterData> characters = new ArrayList<>();
    public final Inventory bank = new Inventory((int) Constants.MAX_BANK);

    public String username, password;

    public Account() {
        this.username = "niL";
        this.password = "niL";

        for(var i = 0; i < 3; ++i)
            characters.add(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;
        return Objects.equals(characters, account.characters)
            && Objects.equals(bank, account.bank)
            && Objects.equals(username, account.username)
            && Objects.equals(password, account.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characters, bank, username, password);
    }
}

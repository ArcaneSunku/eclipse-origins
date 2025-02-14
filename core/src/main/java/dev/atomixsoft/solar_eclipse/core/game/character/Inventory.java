package dev.atomixsoft.solar_eclipse.core.game.character;

import dev.atomixsoft.solar_eclipse.core.game.Constants;
import dev.atomixsoft.solar_eclipse.core.game.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Inventory {

    public final List<Item> sack = new ArrayList<>();
    public final int maxCapacity;

    public Inventory() {
        this((int) Constants.MAX_INV);
    }

    public Inventory(int maxInv) {
        for(var i = 0; i < maxInv; ++i)
            sack.add(i, null);

        maxCapacity = maxInv;
    }

    public boolean isFull() {
        return sack.size() == maxCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Inventory inventory = (Inventory) o;
        return maxCapacity == inventory.maxCapacity && Objects.equals(sack, inventory.sack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sack, maxCapacity);
    }

}

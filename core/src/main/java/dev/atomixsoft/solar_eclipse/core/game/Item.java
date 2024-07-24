package dev.atomixsoft.solar_eclipse.core.game;

import java.util.Objects;

public class Item {

    public String name;
    public String textureName;

    public boolean inWorld;
    public int worldX, worldY;

    public int stack, maxStack;
    public boolean stackable;

    public Item() {
        name = "niL";
        textureName = "niL";

        inWorld = false;
        worldX = worldY = 0;

        stack = 0;
        maxStack = 1;
        stackable = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;
        return stack == item.stack && stackable == item.stackable && Objects.equals(name, item.name) && Objects.equals(textureName, item.textureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, textureName, stack, stackable);
    }

}

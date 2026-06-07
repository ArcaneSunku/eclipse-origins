package dev.atomixsoft.solar_eclipse.core.game.character;

import dev.atomixsoft.solar_eclipse.core.game.Constants;

import java.util.Objects;

public class CharacterData {

    public final Inventory inventory = new Inventory();
    public final Stats stats = new Stats();

    public int textureId;
    public String name;
    public boolean removed;

    public Direction facing;
    public int keyFrame;

    public int x, y;
    public byte sex;
    public boolean player, moving;

    public CharacterData() {
        this.textureId = 1;
        this.name = "niL";

        this.facing = Direction.DOWN;
        this.keyFrame = 0;

        this.x = this.y = 0;
        this.sex = Constants.SEX_MALE;
        this.player = false;
        this.moving = false;
        this.removed = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterData character = (CharacterData) o;

        return textureId == character.textureId &&
               removed == character.removed &&
               keyFrame == character.keyFrame &&
               x == character.x && y == character.y &&
               sex == character.sex && player == character.player &&
               Objects.equals(inventory, character.inventory) && Objects.equals(stats, character.stats) &&
               Objects.equals(name, character.name) && facing == character.facing;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventory, stats, textureId, name, removed, facing, keyFrame, x, y, sex, player);
    }
}

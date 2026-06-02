package dev.atomixsoft.solar_eclipse.core.game.character;

public enum Direction {
    UP((byte) 0), DOWN((byte) 1),
    LEFT((byte) 2), RIGHT((byte) 3);

    final byte ID;
    Direction(byte id) {
        this.ID = id;
    }

    public byte asByte() {
        return ID;
    }

    public static Direction Get(byte id) {
        switch (id) {
            case 0 -> {
                return UP;
            }

            case 1 -> {
                return DOWN;
            }

            case 2 -> {
                return LEFT;
            }

            case 3 -> {
                return RIGHT;
            }

            default -> throw new IllegalStateException("Invalid byte data for Direction!");
        }
    }
}

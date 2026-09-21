package ru.mipt.bit.platformer.level;

public final class LevelMap {
    private final int width;
    private final int height;

    public LevelMap(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level dimensions must be positive");
        }
        this.width = width;
        this.height = height;
    }

    public boolean containsTile(int tileX, int tileY) {
        return tileX >= 0 && tileX < width && tileY >= 0 && tileY < height;
    }
}

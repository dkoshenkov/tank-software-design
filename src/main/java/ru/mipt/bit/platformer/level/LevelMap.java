package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.TileBounds;

public final class LevelMap implements TileBounds {
    private final int width;
    private final int height;

    public LevelMap(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level dimensions must be positive");
        }
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean containsTile(GridPoint2 tileCoordinates) {
        return tileCoordinates.x >= 0 && tileCoordinates.x < width
                && tileCoordinates.y >= 0 && tileCoordinates.y < height;
    }
}

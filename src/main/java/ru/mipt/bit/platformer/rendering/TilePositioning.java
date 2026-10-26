package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;

import static com.badlogic.gdx.math.MathUtils.lerp;

public final class TilePositioning {
    private final float tileWidth;
    private final float tileHeight;

    public TilePositioning(float tileWidth, float tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public void centerOnTile(Rectangle worldBounds, GridPoint2 tileCoordinates) {
        worldBounds.setCenter(
                tileCenter(tileCoordinates.x, tileWidth),
                tileCenter(tileCoordinates.y, tileHeight));
    }

    public void interpolateCenter(Rectangle worldBounds, GridPoint2 from, GridPoint2 to, float progress) {
        worldBounds.setCenter(
                interpolateCenter(from.x, to.x, tileWidth, progress),
                interpolateCenter(from.y, to.y, tileHeight, progress));
    }

    private float interpolateCenter(int from, int to, float tileSize, float progress) {
        return lerp(tileCenter(from, tileSize), tileCenter(to, tileSize), progress);
    }

    private float tileCenter(int coordinate, float tileSize) {
        return coordinate * tileSize + tileSize / 2f;
    }
}

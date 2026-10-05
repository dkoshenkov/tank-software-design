package ru.mipt.bit.platformer.grid;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;

public final class TilePositioning {
    private final float tileWidth;
    private final float tileHeight;

    public TilePositioning(float tileWidth, float tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public void centerOnTile(Rectangle worldBounds, GridPoint2 tileCoordinates) {
        worldBounds.setCenter(tileCenterX(tileCoordinates), tileCenterY(tileCoordinates));
    }

    public float tileCenterX(GridPoint2 tileCoordinates) {
        return tileCoordinates.x * tileWidth + tileWidth / 2f;
    }

    public float tileCenterY(GridPoint2 tileCoordinates) {
        return tileCoordinates.y * tileHeight + tileHeight / 2f;
    }
}

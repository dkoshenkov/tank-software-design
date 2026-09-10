package ru.mipt.bit.platformer.grid;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;

public final class TilePositioning {
    private final TiledMapTileLayer tileLayer;

    public TilePositioning(TiledMapTileLayer tileLayer) {
        this.tileLayer = tileLayer;
    }

    public void centerOnTile(Rectangle worldBounds, GridPoint2 tileCoordinates) {
        worldBounds.setCenter(tileCenterX(tileCoordinates), tileCenterY(tileCoordinates));
    }

    public void positionBetweenTiles(Rectangle worldBounds,
                                     GridPoint2 fromTileCoordinates, GridPoint2 toTileCoordinates,
                                     float progress, Interpolation interpolation) {
        worldBounds.setCenter(
                interpolation.apply(tileCenterX(fromTileCoordinates), tileCenterX(toTileCoordinates), progress),
                interpolation.apply(tileCenterY(fromTileCoordinates), tileCenterY(toTileCoordinates), progress));
    }

    private float tileCenterX(GridPoint2 tileCoordinates) {
        return tileCoordinates.x * tileLayer.getTileWidth() + tileLayer.getTileWidth() / 2f;
    }

    private float tileCenterY(GridPoint2 tileCoordinates) {
        return tileCoordinates.y * tileLayer.getTileHeight() + tileLayer.getTileHeight() / 2f;
    }
}

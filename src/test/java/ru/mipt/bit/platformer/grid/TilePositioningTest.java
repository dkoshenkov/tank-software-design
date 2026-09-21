package ru.mipt.bit.platformer.grid;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class TilePositioningTest {
    private final TilePositioning positioning = new TilePositioning(new TiledMapTileLayer(10, 8, 128, 64));

    @Test public void centersNonSquareSpriteOnNonSquareTile() {
        Rectangle bounds = new Rectangle(0, 0, 20, 10);
        positioning.centerOnTile(bounds, new GridPoint2(2, 3));
        assertEquals(310f, bounds.x, 0f);
        assertEquals(219f, bounds.y, 0f);
        assertEquals(20f, bounds.width, 0f);
        assertEquals(10f, bounds.height, 0f);
    }

    @Test public void interpolatesEndpointsAndIntermediatePosition() {
        Rectangle bounds = new Rectangle(0, 0, 20, 10);
        GridPoint2 from = new GridPoint2(0, 0);
        GridPoint2 to = new GridPoint2(2, 2);
        positioning.positionBetweenTiles(bounds, from, to, 0f, Interpolation.linear);
        assertEquals(54f, bounds.x, 0f);
        assertEquals(27f, bounds.y, 0f);
        positioning.positionBetweenTiles(bounds, from, to, 0.25f, Interpolation.linear);
        assertEquals(118f, bounds.x, 0f);
        assertEquals(59f, bounds.y, 0f);
        positioning.positionBetweenTiles(bounds, from, to, 1f, Interpolation.linear);
        assertEquals(310f, bounds.x, 0f);
        assertEquals(155f, bounds.y, 0f);
        assertEquals(new GridPoint2(0, 0), from);
        assertEquals(new GridPoint2(2, 2), to);
    }
}

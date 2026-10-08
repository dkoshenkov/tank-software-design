package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.math.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class TilePositioningTest {
    private final TilePositioning positioning = new TilePositioning(128, 64);

    @Test public void centersNonSquareSpriteOnNonSquareTile() {
        Rectangle bounds = new Rectangle(0, 0, 20, 10);
        positioning.centerOnTile(bounds, new GridPoint2(2, 3));
        assertEquals(310f, bounds.x, 0f);
        assertEquals(219f, bounds.y, 0f);
        assertEquals(20f, bounds.width, 0f);
        assertEquals(10f, bounds.height, 0f);
    }

    @Test public void calculatesTileCenterWithoutChangingCoordinates() {
        GridPoint2 coordinates = new GridPoint2(2, 3);
        assertEquals(320f, positioning.tileCenterX(coordinates), 0f);
        assertEquals(224f, positioning.tileCenterY(coordinates), 0f);
        assertEquals(new GridPoint2(2, 3), coordinates);
    }
}

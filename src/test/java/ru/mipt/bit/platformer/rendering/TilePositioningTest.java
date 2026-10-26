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

    @Test public void interpolatesCenterWithoutChangingTileCoordinatesOrSpriteSize() {
        GridPoint2 from = new GridPoint2(2, 3);
        GridPoint2 to = new GridPoint2(3, 4);
        Rectangle bounds = new Rectangle(0, 0, 20, 10);
        float[] progress = {0f, 0.5f, 1f};
        float[] expectedX = {310f, 374f, 438f};
        float[] expectedY = {219f, 251f, 283f};
        for (int i = 0; i < progress.length; i++) {
            positioning.interpolateCenter(bounds, from, to, progress[i]);
            assertEquals(expectedX[i], bounds.x, 0f);
            assertEquals(expectedY[i], bounds.y, 0f);
            assertEquals(20f, bounds.width, 0f);
            assertEquals(10f, bounds.height, 0f);
        }
        assertEquals(new GridPoint2(2, 3), from);
        assertEquals(new GridPoint2(3, 4), to);
    }
}

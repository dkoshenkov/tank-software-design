package ru.mipt.bit.platformer.level;

import org.junit.Test;

import static org.junit.Assert.*;

public class LevelMapTest {
    @Test public void keepsDimensionsInTiles() {
        LevelMap level = new LevelMap(10, 8);
        assertEquals(10, level.getWidth());
        assertEquals(8, level.getHeight());
    }

    @Test public void containsCornersButRejectsAllFourOutsideEdges() {
        LevelMap level = new LevelMap(3, 2);
        assertTrue(level.containsTile(0, 0));
        assertTrue(level.containsTile(2, 0));
        assertTrue(level.containsTile(0, 1));
        assertTrue(level.containsTile(2, 1));
        assertFalse(level.containsTile(-1, 0));
        assertFalse(level.containsTile(0, -1));
        assertFalse(level.containsTile(3, 0));
        assertFalse(level.containsTile(0, 2));
    }

    @Test public void singleTileLevelContainsOnlyOrigin() {
        LevelMap level = new LevelMap(1, 1);
        assertTrue(level.containsTile(0, 0));
        assertFalse(level.containsTile(1, 0));
        assertFalse(level.containsTile(0, 1));
    }

    @Test public void rejectsNonPositiveDimensions() {
        int[][] invalid = {{0, 8}, {10, 0}, {-1, 8}, {10, -1}};
        for (int[] size : invalid) {
            assertThrows(IllegalArgumentException.class, () -> new LevelMap(size[0], size[1]));
        }
    }
}

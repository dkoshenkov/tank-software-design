package ru.mipt.bit.platformer.model.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class LevelMapTest {
    @Test public void containsCornersButRejectsAllFourOutsideEdges() {
        LevelMap level = new LevelMap(3, 2);
        assertTrue(level.containsTile(new GridPoint2(0, 0)));
        assertTrue(level.containsTile(new GridPoint2(2, 0)));
        assertTrue(level.containsTile(new GridPoint2(0, 1)));
        assertTrue(level.containsTile(new GridPoint2(2, 1)));
        assertFalse(level.containsTile(new GridPoint2(-1, 0)));
        assertFalse(level.containsTile(new GridPoint2(0, -1)));
        assertFalse(level.containsTile(new GridPoint2(3, 0)));
        assertFalse(level.containsTile(new GridPoint2(0, 2)));
    }

    @Test public void rejectsNonPositiveDimensions() {
        int[][] invalid = {{0, 8}, {10, 0}, {-1, 8}, {10, -1}};
        for (int[] size : invalid) {
            assertThrows(IllegalArgumentException.class, () -> new LevelMap(size[0], size[1]));
        }
    }
}

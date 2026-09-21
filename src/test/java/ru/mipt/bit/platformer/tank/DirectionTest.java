package ru.mipt.bit.platformer.tank;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import static org.junit.Assert.*;

public class DirectionTest {
    @Test public void directionsHaveExpectedOffsetsAndAnglesWithoutMutatingOrigin() {
        GridPoint2 origin = new GridPoint2(3, 4);
        assertEquals(new GridPoint2(3, 5), Direction.UP.getAdjacentTileCoordinates(origin));
        assertEquals(new GridPoint2(2, 4), Direction.LEFT.getAdjacentTileCoordinates(origin));
        assertEquals(new GridPoint2(3, 3), Direction.DOWN.getAdjacentTileCoordinates(origin));
        assertEquals(new GridPoint2(4, 4), Direction.RIGHT.getAdjacentTileCoordinates(origin));
        assertEquals(new GridPoint2(3, 4), origin);
        assertEquals(90f, Direction.UP.getRotationDegrees(), 0f);
        assertEquals(-180f, Direction.LEFT.getRotationDegrees(), 0f);
        assertEquals(-90f, Direction.DOWN.getRotationDegrees(), 0f);
        assertEquals(0f, Direction.RIGHT.getRotationDegrees(), 0f);
    }
}

package ru.mipt.bit.platformer.model.obstacle;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import static org.junit.Assert.*;

public class TreeTest {
    @Test public void occupiesOnlyItsOwnTile() {
        Tree obstacle = new Tree(1, 3);
        assertTrue(obstacle.occupiesTile(new GridPoint2(1, 3)));
        assertFalse(obstacle.occupiesTile(new GridPoint2(1, 2)));
        assertFalse(obstacle.occupiesTile(new GridPoint2(2, 3)));
    }
    @Test public void coordinatesAreDefensivelyCopied() {
        Tree obstacle = new Tree(1, 3);
        obstacle.getTileCoordinates().set(0, 0);
        assertEquals(new GridPoint2(1, 3), obstacle.getTileCoordinates());
    }
}

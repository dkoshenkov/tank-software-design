package ru.mipt.bit.platformer.tank;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import static org.junit.Assert.*;

public class TankTest {
    @Test public void startsIdleAtRequestedPositionFacingRight() {
        Tank tank = new Tank(2, 3);
        assertEquals(new GridPoint2(2, 3), tank.getTileCoordinates());
        assertEquals(new GridPoint2(2, 3), tank.getDestinationTileCoordinates());
        assertEquals(1f, tank.getMovementProgress(), 0f);
        assertEquals(0f, tank.getRotationDegrees(), 0f);
        tank.update(1f);
        assertEquals(new GridPoint2(2, 3), tank.getTileCoordinates());
    }

    @Test public void movesOverTimeAndCommitsPositionOnlyAtDestination() {
        Tank tank = new Tank(2, 3);
        tank.tryMove(Direction.UP, tile -> {
            assertEquals(new GridPoint2(2, 4), tile);
            return true;
        });
        assertEquals(0f, tank.getMovementProgress(), 0f);
        assertEquals(new GridPoint2(2, 4), tank.getDestinationTileCoordinates());
        tank.update(0.2f);
        assertEquals(0.5f, tank.getMovementProgress(), 0.00001f);
        assertEquals(new GridPoint2(2, 3), tank.getTileCoordinates());
        tank.update(0.2f);
        assertEquals(1f, tank.getMovementProgress(), 0f);
        assertEquals(new GridPoint2(2, 4), tank.getTileCoordinates());
    }

    @Test public void blockedMoveTurnsTankButDoesNotStartMovement() {
        Tank tank = new Tank(2, 3);
        tank.tryMove(Direction.LEFT, tile -> false);
        assertEquals(1f, tank.getMovementProgress(), 0f);
        assertEquals(-180f, tank.getRotationDegrees(), 0f);
        assertEquals(new GridPoint2(2, 3), tank.getDestinationTileCoordinates());
        tank.update(1f);
        assertEquals(new GridPoint2(2, 3), tank.getTileCoordinates());
    }

    @Test public void ignoresNewCommandsWhileMoving() {
        Tank tank = new Tank(2, 3);
        tank.tryMove(Direction.UP, tile -> true);
        tank.update(0.1f);
        tank.tryMove(Direction.LEFT, tile -> { fail("Must not check a new move while moving"); return true; });
        assertEquals(90f, tank.getRotationDegrees(), 0f);
        assertEquals(new GridPoint2(2, 4), tank.getDestinationTileCoordinates());
        assertEquals(0.25f, tank.getMovementProgress(), 0.00001f);
    }

    @Test public void largeDeltaCompletesExactlyOneMoveAndAllowsNextMove() {
        Tank tank = new Tank(2, 3);
        tank.tryMove(Direction.RIGHT, tile -> true);
        tank.update(10f);
        assertEquals(1f, tank.getMovementProgress(), 0f);
        assertEquals(new GridPoint2(3, 3), tank.getTileCoordinates());
        tank.tryMove(Direction.DOWN, tile -> true);
        assertEquals(0f, tank.getMovementProgress(), 0f);
        assertEquals(new GridPoint2(3, 2), tank.getDestinationTileCoordinates());
    }

    @Test public void returnedCoordinatesCannotMutateTank() {
        Tank tank = new Tank(2, 3);
        tank.getTileCoordinates().set(100, 100);
        tank.getDestinationTileCoordinates().set(-1, -1);
        assertEquals(new GridPoint2(2, 3), tank.getTileCoordinates());
        assertEquals(new GridPoint2(2, 3), tank.getDestinationTileCoordinates());
    }
}

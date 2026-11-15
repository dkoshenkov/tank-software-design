package ru.mipt.bit.platformer.model.tank;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.bullet.Bullet;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TankTest {
    @Test public void startsWith100Health() {
        assertEquals(100, new Tank(2, 3).getHealth());
        assertEquals(100, new Tank(2, 3, 0.8f).getHealth());
    }

    @Test public void maximumHealthRemainsInitialHealthAfterDamage() {
        Tank tank = new Tank(2, 3, 0.4f, 80);
        assertEquals(80, tank.getHealth());
        assertEquals(80, tank.getMaxHealth());
        tank.takeDamage(20);
        assertEquals(60, tank.getHealth());
        assertEquals(80, tank.getMaxHealth());
        assertEquals(100, new Tank(0, 0).getMaxHealth());
    }

    @Test public void movementSpeedDependsOnFractionOfInitialHealth() {
        assertMovementAtHealth(141, 0.5f);
        assertMovementAtHealth(140, 0.25f);
        assertMovementAtHealth(30, 0.25f);
        assertMovementAtHealth(29, 0.5f / 3f);
    }

    private void assertMovementAtHealth(int health, float expectedProgress) {
        Tank tank = new Tank(0, 0, 1f, 200);
        tank.takeDamage(200 - health);
        tank.tryMove(Direction.RIGHT, tile -> true);
        tank.update(0.5f);
        assertEquals("Remaining health: " + health, expectedProgress, tank.getMovementProgress(), 0.00001f);
    }

    @Test public void canShootAtFifteenPercentHealthButNotBelow() {
        Tank tank = new Tank(0, 0, 1f, 200);
        List<Bullet> shots = new ArrayList<>();
        tank.shoot(shots::add);
        tank.takeDamage(170);
        tank.shoot(shots::add);
        assertEquals(2, shots.size());

        tank.takeDamage(1);
        tank.shoot(shots::add);
        assertEquals(2, shots.size());
    }

    @Test public void tankStartingWithZeroHealthMovesAtHeavySpeedAndCannotShoot() {
        Tank tank = new Tank(0, 0, 1f, 0);
        tank.shoot(bullet -> fail("Heavy tank fired"));
        tank.tryMove(Direction.RIGHT, tile -> true);
        tank.update(1.5f);
        assertEquals(0.5f, tank.getMovementProgress(), 0.00001f);
    }

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

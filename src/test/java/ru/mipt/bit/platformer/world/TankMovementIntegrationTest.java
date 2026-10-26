package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.input.command.MoveCommand;
import ru.mipt.bit.platformer.model.level.LevelBounds;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.util.List;

import static org.junit.Assert.*;

public class TankMovementIntegrationTest {
    @Test public void commandReservesBothTilesAndReleasesOriginOnArrival() {
        Tank tank = new Tank(1, 1);
        GameWorld world = new GameWorld(List.of(tank), new LevelBounds(4, 3), List.of(tank));
        MoveCommand command = new MoveCommand(tank, Direction.RIGHT, world::canMoveTo);
        command.run();
        assertTrue(tank.isMoving());
        assertFalse(world.canMoveTo(new GridPoint2(1, 1)));
        assertFalse(world.canMoveTo(new GridPoint2(2, 1)));
        world.update(0.2f);
        assertTrue(tank.occupiesTile(new GridPoint2(1, 1)));
        assertTrue(tank.occupiesTile(new GridPoint2(2, 1)));
        world.update(0.2f);
        assertFalse(tank.isMoving());
        assertTrue(world.canMoveTo(new GridPoint2(1, 1)));
        assertFalse(world.canMoveTo(new GridPoint2(2, 1)));
        command.run();
        assertEquals(new GridPoint2(3, 1), tank.getDestinationTileCoordinates());
    }

    @Test public void firstCommandWinsWhenTwoTanksWantSameTile() {
        Tank left = new Tank(0, 0);
        Tank right = new Tank(2, 0);
        GameWorld world = new GameWorld(List.of(left, right), new LevelBounds(3, 1), List.of(left, right));
        new MoveCommand(left, Direction.RIGHT, world::canMoveTo).run();
        new MoveCommand(right, Direction.LEFT, world::canMoveTo).run();
        world.update(0.4f);
        assertEquals(new GridPoint2(1, 0), left.getTileCoordinates());
        assertEquals(new GridPoint2(2, 0), right.getTileCoordinates());
    }

    @Test public void tanksCannotSwapCellsOrEnterMovingTankOrigin() {
        Tank first = new Tank(0, 0);
        Tank second = new Tank(1, 0);
        GameWorld world = new GameWorld(List.of(first, second), new LevelBounds(3, 1), List.of(first, second));
        new MoveCommand(first, Direction.RIGHT, world::canMoveTo).run();
        new MoveCommand(second, Direction.LEFT, world::canMoveTo).run();
        assertFalse(first.isMoving());
        assertFalse(second.isMoving());
        new MoveCommand(second, Direction.RIGHT, world::canMoveTo).run();
        new MoveCommand(first, Direction.RIGHT, world::canMoveTo).run();
        assertFalse(first.isMoving());
        world.update(0.4f);
        new MoveCommand(first, Direction.RIGHT, world::canMoveTo).run();
        assertTrue(first.isMoving());
    }

}

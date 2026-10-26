package ru.mipt.bit.platformer.input.ai;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.input.TankControlStrategy;
import ru.mipt.bit.platformer.input.command.MoveCommand;
import ru.mipt.bit.platformer.input.keyboard.TankKeyboardController;
import ru.mipt.bit.platformer.input.keyboard.TestInput;
import ru.mipt.bit.platformer.model.level.LevelBounds;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;
import ru.mipt.bit.platformer.world.GameWorld;

import java.util.List;
import java.util.Random;

import static com.badlogic.gdx.Input.Keys.RIGHT;
import static org.junit.Assert.*;

public class TankControlIntegrationTest {
    @Test public void strategiesControlOnlyTheirOwnTankAndAiWaitsForArrival() {
        Tank player = new Tank(1, 0);
        Tank ai = new Tank(0, 1);
        GameWorld world = new GameWorld(List.of(player, ai), new LevelBounds(4, 2), List.of(player, ai));
        Random random = new Random() {
            @Override
            public int nextInt(int bound) {
                return Direction.DOWN.ordinal();
            }
        };
        TestInput input = new TestInput();
        TankControlStrategy keyboard = new TankKeyboardController(input.input, direction ->
                new MoveCommand(player, direction, world::canMoveTo).run());
        TankControlStrategy computer = new RandomTankController(ai, world::canMoveTo, random);
        input.held.add(RIGHT);
        keyboard.controlTank();
        assertFalse(ai.isMoving());
        computer.controlTank();
        assertEquals(new GridPoint2(0, 0), ai.getDestinationTileCoordinates());
        computer.controlTank();
        assertEquals(new GridPoint2(0, 1), ai.getTileCoordinates());
        assertEquals(new GridPoint2(0, 0), ai.getDestinationTileCoordinates());
        world.update(0.4f);
        assertEquals(new GridPoint2(2, 0), player.getTileCoordinates());
        assertEquals(new GridPoint2(0, 0), ai.getTileCoordinates());
    }

    @Test public void multipleAiTanksKeepCellsExclusiveOverManyFrames() {
        List<Tank> tanks = List.of(new Tank(0, 0), new Tank(2, 0), new Tank(1, 2));
        GameWorld world = new GameWorld(new java.util.ArrayList<>(tanks), new LevelBounds(3, 3),
                new java.util.ArrayList<>(tanks));
        List<TankControlStrategy> controls = List.of(
                new RandomTankController(tanks.get(0), world::canMoveTo, new Random(1)),
                new RandomTankController(tanks.get(1), world::canMoveTo, new Random(2)),
                new RandomTankController(tanks.get(2), world::canMoveTo, new Random(3)));
        boolean moved = false;
        for (int frame = 0; frame < 500; frame++) {
            for (TankControlStrategy control : controls) {
                control.controlTank();
            }
            for (Tank tank : tanks) {
                moved |= tank.isMoving();
                for (Tank other : tanks) {
                    if (tank != other) {
                        assertFalse(other.occupiesTile(tank.getTileCoordinates()));
                        assertFalse(other.occupiesTile(tank.getDestinationTileCoordinates()));
                    }
                }
                GridPoint2 target = tank.getDestinationTileCoordinates();
                assertTrue(target.x >= 0 && target.x < 3 && target.y >= 0 && target.y < 3);
            }
            world.update(0.1f);
        }
        assertTrue(moved);
    }

}

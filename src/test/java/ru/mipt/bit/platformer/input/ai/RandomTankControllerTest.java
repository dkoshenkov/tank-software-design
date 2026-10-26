package ru.mipt.bit.platformer.input.ai;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.input.TankControlStrategy;
import ru.mipt.bit.platformer.model.level.LevelBounds;
import ru.mipt.bit.platformer.model.obstacle.Tree;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;
import ru.mipt.bit.platformer.world.GameWorld;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import static org.junit.Assert.*;

public class RandomTankControllerTest {
    @Test public void waitsForArrivalBeforeIssuingAnotherMove() {
        Tank tank = new Tank(0, 0) {
            @Override
            public void tryMove(Direction direction, Predicate<GridPoint2> canMoveTo) {
                assertFalse("AI must wait for arrival before issuing a move", isMoving());
                super.tryMove(direction, canMoveTo);
            }
        };
        Random random = new Random() {
            @Override
            public int nextInt(int bound) {
                return Direction.RIGHT.ordinal();
            }
        };
        RandomTankController controller = new RandomTankController(tank, tile -> true, random);
        controller.controlTank();
        tank.update(0.2f);
        controller.controlTank();
        assertEquals(new GridPoint2(1, 0), tank.getDestinationTileCoordinates());
        assertEquals(0.5f, tank.getMovementProgress(), 0f);
        tank.update(0.2f);
        controller.controlTank();
        assertEquals(new GridPoint2(2, 0), tank.getDestinationTileCoordinates());
    }

    @Test public void aiRespectsObstaclesAndAllMapEdges() {
        Tank tank = new Tank(0, 0);
        GameWorld world = new GameWorld(List.of(tank), new LevelBounds(2, 1), List.of(tank, new Tree(1, 0)));
        TankControlStrategy ai = new RandomTankController(tank, world::canMoveTo, new Random(42));
        for (int i = 0; i < 100; i++) {
            ai.controlTank();
            world.update(0.4f);
            assertEquals(new GridPoint2(0, 0), tank.getTileCoordinates());
            assertFalse(tank.isMoving());
        }
    }
}

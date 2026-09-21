package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.level.LevelMap;
import ru.mipt.bit.platformer.tank.*;
import ru.mipt.bit.platformer.obstacle.Obstacle;
import static org.junit.Assert.*;

public class GameWorldTest {
    @Test public void rejectsAllFourOutsideEdgesAndAllowsCorners() {
        GameWorld world = new GameWorld(new Tank(0, 0), new Obstacle(1, 3), new LevelMap(10, 8));
        int[][] outside = {{-1,0}, {0,-1}, {10,0}, {0,8}, {100,100}};
        for (int[] p : outside) assertFalse(world.canMoveTo(new GridPoint2(p[0], p[1])));
        int[][] corners = {{0,0}, {9,0}, {0,7}, {9,7}};
        for (int[] p : corners) assertTrue(world.canMoveTo(new GridPoint2(p[0], p[1])));
        assertFalse(world.canMoveTo(new GridPoint2(1, 3)));
    }

    @Test public void respectsDimensionsOfAnotherMap() {
        GameWorld world = new GameWorld(new Tank(0, 0), new Obstacle(1, 1), new LevelMap(3, 2));
        assertTrue(world.canMoveTo(new GridPoint2(2, 1)));
        assertFalse(world.canMoveTo(new GridPoint2(3, 1)));
        assertFalse(world.canMoveTo(new GridPoint2(2, 2)));
    }

    @Test public void tankCannotLeaveMapButCanMoveInside() {
        Tank tank = new Tank(0, 0);
        Obstacle obstacle = new Obstacle(1, 3);
        GameWorld world = new GameWorld(tank, obstacle, new LevelMap(10, 8));
        tank.tryMove(Direction.LEFT, world::canMoveTo);
        world.update(0.4f);
        assertEquals(new GridPoint2(0, 0), tank.getTileCoordinates());
        tank.tryMove(Direction.RIGHT, world::canMoveTo);
        world.update(0.4f);
        assertEquals(new GridPoint2(1, 0), tank.getTileCoordinates());
        assertSame(tank, world.getTank());
        assertSame(obstacle, world.getObstacle());
    }

    @Test public void tankStopsBeforeObstacleAndCanMoveAroundIt() {
        Tank tank = new Tank(1, 1);
        GameWorld world = new GameWorld(tank, new Obstacle(1, 3), new LevelMap(10, 8));
        tank.tryMove(Direction.UP, world::canMoveTo);
        world.update(0.4f);
        assertEquals(new GridPoint2(1, 2), tank.getTileCoordinates());

        tank.tryMove(Direction.UP, world::canMoveTo);
        world.update(0.4f);
        assertEquals(new GridPoint2(1, 2), tank.getTileCoordinates());

        tank.tryMove(Direction.RIGHT, world::canMoveTo);
        world.update(0.4f);
        tank.tryMove(Direction.UP, world::canMoveTo);
        world.update(0.4f);
        assertEquals(new GridPoint2(2, 3), tank.getTileCoordinates());
    }
}

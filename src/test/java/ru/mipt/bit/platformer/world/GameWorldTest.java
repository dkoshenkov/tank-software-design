package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.level.LevelMap;
import ru.mipt.bit.platformer.tank.*;
import ru.mipt.bit.platformer.obstacle.Tree;
import ru.mipt.bit.platformer.model.TileBlocker;
import ru.mipt.bit.platformer.model.TileBounds;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class GameWorldTest {
    @Test public void updatesRegisteredObjectsInOrderWithSameDeltaAndCopiedList() {
        List<String> calls = new ArrayList<>();
        List<ru.mipt.bit.platformer.model.Updatable> updateList = new ArrayList<>();
        updateList.add(delta -> calls.add("first:" + delta));
        updateList.add(delta -> calls.add("second:" + delta));
        GameWorld world = new GameWorld(updateList, new LevelMap(2, 2), List.of());
        updateList.clear();

        world.update(0.25f);

        assertEquals(List.of("first:0.25", "second:0.25"), calls);
    }

    @Test public void tanksAdvanceIndependentlyWithDifferentMoveDurations() {
        Tank fast = new Tank(0, 0, 0.4f);
        Tank slow = new Tank(2, 0, 0.8f);
        fast.tryMove(Direction.RIGHT, tile -> true);
        slow.tryMove(Direction.RIGHT, tile -> true);
        GameWorld world = new GameWorld(List.of(fast, slow), new LevelMap(10, 8), List.of());

        world.update(0.4f);
        assertEquals(1f, fast.getMovementProgress(), 0f);
        assertEquals(0.5f, slow.getMovementProgress(), 0.00001f);
        assertEquals(new GridPoint2(1, 0), fast.getTileCoordinates());
        assertEquals(new GridPoint2(2, 0), slow.getTileCoordinates());

        world.update(0.4f);
        assertEquals(1f, slow.getMovementProgress(), 0f);
        assertEquals(new GridPoint2(3, 0), slow.getTileCoordinates());
    }

    @Test public void rejectsAllFourOutsideEdgesAndAllowsCorners() {
        GameWorld world = new GameWorld(List.of(new Tank(0, 0)), new LevelMap(10, 8),
                List.of(new Tree(1, 3)));
        int[][] outside = {{-1,0}, {0,-1}, {10,0}, {0,8}, {100,100}};
        for (int[] p : outside) assertFalse(world.canMoveTo(new GridPoint2(p[0], p[1])));
        int[][] corners = {{0,0}, {9,0}, {0,7}, {9,7}};
        for (int[] p : corners) assertTrue(world.canMoveTo(new GridPoint2(p[0], p[1])));
        assertFalse(world.canMoveTo(new GridPoint2(1, 3)));
    }

    @Test public void tankCannotLeaveMapButCanMoveInside() {
        Tank tank = new Tank(0, 0);
        TileBlocker obstacle = new Tree(1, 3);
        GameWorld world = new GameWorld(List.of(tank), new LevelMap(10, 8), List.of(obstacle));
        tank.tryMove(Direction.LEFT, world::canMoveTo);
        world.update(0.4f);
        assertEquals(new GridPoint2(0, 0), tank.getTileCoordinates());
        tank.tryMove(Direction.RIGHT, world::canMoveTo);
        world.update(0.4f);
        assertEquals(new GridPoint2(1, 0), tank.getTileCoordinates());
    }

    @Test public void tankStopsBeforeObstacleAndCanMoveAroundIt() {
        Tank tank = new Tank(1, 1);
        GameWorld world = new GameWorld(List.of(tank), new LevelMap(10, 8),
                List.of(new Tree(1, 3)));
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

    @Test public void supportsMultipleObstacles() {
        TileBlocker secondObstacle = new Tree(2, 3);
        GameWorld world = new GameWorld(List.of(new Tank(0, 0)), new LevelMap(10, 8),
                List.of(new Tree(1, 3), secondObstacle));

        assertFalse(world.canMoveTo(new GridPoint2(1, 3)));
        assertFalse(world.canMoveTo(new GridPoint2(2, 3)));
        assertTrue(world.canMoveTo(new GridPoint2(3, 3)));
    }

    @Test public void usesInjectedBoundsAndKeepsItsBlockerSet() {
        TileBounds onlyFirstColumn = tile -> tile.x == 0 && tile.y >= 0 && tile.y < 3;
        List<TileBlocker> blockers = new ArrayList<>();
        blockers.add(new Tree(0, 1));
        GameWorld world = new GameWorld(List.of(new Tank(0, 0)), onlyFirstColumn, blockers);
        blockers.clear();

        assertTrue(world.canMoveTo(new GridPoint2(0, 0)));
        assertFalse(world.canMoveTo(new GridPoint2(0, 1)));
        assertFalse(world.canMoveTo(new GridPoint2(1, 0)));
    }
}

package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.TilePosition;
import ru.mipt.bit.platformer.model.bullet.Bullet;
import ru.mipt.bit.platformer.model.level.LevelBounds;
import ru.mipt.bit.platformer.model.obstacle.Tree;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;
import ru.mipt.bit.platformer.rendering.GameWorldRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class WorldEventsTest {
    private final GameWorld world = new GameWorld(new LevelBounds(4, 2));

    @Test public void removalByTheFirstListenerReachesEveryoneAfterTheAddAndCancelsTheHit() {
        new CombatSystem(world);
        Tank target = new Tank(1, 0);
        Bullet shot = new Bullet(new Tank(0, 0), target.getTileCoordinates(), Direction.RIGHT, 30);
        world.add(target);
        List<String> events = new ArrayList<>();
        world.addListener(listener(object -> {
            events.add("first add");
            if (object == shot) {
                world.remove(shot);
            }
        }, object -> events.add("first remove")));
        world.addListener(listener(object -> events.add("second add"), object -> events.add("second remove")));
        events.clear();

        world.add(shot);

        assertEquals(List.of("first add", "second add", "first remove", "second remove"), events);
        assertEquals(List.of(target), world.objects());
        assertEquals(100, target.getHealth());
    }

    @Test public void aViewFactoryFailureRollsBackAllRolesAndAllowsRetry() {
        Tank tank = new Tank(0, 0);
        tank.tryMove(Direction.RIGHT, world::canMoveTo);
        List<TilePosition> visible = new ArrayList<>();
        world.addListener(listener(visible::add, visible::remove));
        IllegalArgumentException failure = new IllegalArgumentException("Missing view");
        GameWorldRenderer renderer = new GameWorldRenderer(null, null, object -> { throw failure; });
        world.addListener(renderer);
        List<String> laterEvents = new ArrayList<>();
        world.addListener(listener(object -> laterEvents.add("add"), object -> laterEvents.add("remove")));

        assertSame(failure, assertThrows(IllegalArgumentException.class, () -> world.add(tank)));
        assertFalse(world.contains(tank));
        assertTrue(world.canMoveTo(tank.getTileCoordinates()));
        assertTrue(world.canMoveTo(tank.getDestinationTileCoordinates()));
        assertTrue(visible.isEmpty());
        assertTrue(laterEvents.isEmpty());
        world.update(0.4f);
        assertEquals(0f, tank.getMovementProgress(), 0f);

        world.removeListener(renderer);
        world.add(tank);
        world.update(0.4f);
        assertEquals(List.of(tank), visible);
        assertEquals(List.of("add"), laterEvents);
        assertEquals(new GridPoint2(1, 0), tank.getTileCoordinates());
        assertFalse(world.canMoveTo(tank.getTileCoordinates()));
    }

    @Test public void failedReplayDetachesTheListenerBeforeItsNestedAdditionIsDelivered() {
        Tree first = new Tree(0, 0);
        Tree second = new Tree(1, 0);
        Tree third = new Tree(2, 0);
        world.add(first);
        world.add(second);
        List<TilePosition> attempts = new ArrayList<>();
        List<TilePosition> visible = new ArrayList<>();
        IllegalStateException failure = new IllegalStateException("Cannot display second tree");
        WorldListener subscriber = listener(object -> {
            attempts.add(object);
            visible.add(object);
            if (object == first) {
                world.add(third);
            } else if (object == second) {
                throw failure;
            }
        }, visible::remove);

        assertSame(failure, assertThrows(IllegalStateException.class, () -> world.addListener(subscriber)));
        assertTrue(visible.isEmpty());
        assertEquals(List.of(first, second, third), world.objects());
        world.remove(third);
        world.add(third);
        assertEquals(List.of(first, second), attempts);
    }

    private static WorldListener listener(Consumer<TilePosition> added, Consumer<TilePosition> removed) {
        return new WorldListener() {
            @Override public void objectAdded(TilePosition object) { added.accept(object); }
            @Override public void objectRemoved(TilePosition object) { removed.accept(object); }
        };
    }
}

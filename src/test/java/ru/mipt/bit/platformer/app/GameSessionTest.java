package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Disposable;
import org.junit.Test;
import ru.mipt.bit.platformer.input.keyboard.TankKeyboardController;
import ru.mipt.bit.platformer.input.keyboard.TestInput;
import ru.mipt.bit.platformer.model.level.LevelBounds;
import ru.mipt.bit.platformer.model.tank.Tank;
import ru.mipt.bit.platformer.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.Input.Keys.RIGHT;
import static org.junit.Assert.*;

public class GameSessionTest {
    @Test public void frameHandlesInputThenUpdatesThenRenders() {
        Tank tank = new Tank(1, 1);
        GameWorld world = new GameWorld(List.of(tank), new LevelBounds(10, 8), List.of());
        TestInput input = new TestInput();
        TankKeyboardController keyboard = new TankKeyboardController(input.input, direction ->
                tank.tryMove(direction, world::canMoveTo));
        input.held.add(RIGHT);
        List<GridPoint2> renderedPositions = new ArrayList<>();
        new GameSession(world, keyboard::controlTank,
                () -> renderedPositions.add(tank.getTileCoordinates()), List.of())
                .renderFrame(0.4f);
        assertEquals(List.of(new GridPoint2(2, 1)), renderedPositions);
    }

    @Test public void frameAcceptsAlternativeInputAndRendering() {
        List<String> stages = new ArrayList<>();
        GameWorld world = new GameWorld(List.of(delta -> {
            assertEquals(0.25f, delta, 0f);
            stages.add("update");
        }), new LevelBounds(10, 8), List.of());
        GameSession session = new GameSession(world,
                () -> stages.add("input"),
                () -> stages.add("render"), List.of());

        session.renderFrame(0.25f);

        assertEquals(List.of("input", "update", "render"), stages);
    }

    @Test public void resourcesAreDisposedInReverseOrderOnlyOnce() {
        List<String> disposed = new ArrayList<>();
        List<Disposable> resources = new ArrayList<>(List.of(
                () -> disposed.add("map"), () -> disposed.add("batch"), () -> disposed.add("texture")));
        GameSession session = new GameSession(null, null, null, resources);
        resources.clear();

        session.dispose();
        session.dispose();

        assertEquals(List.of("texture", "batch", "map"), disposed);
    }

    @Test public void disposalCanResumeAfterFailureWithoutRetryingFailedResource() {
        List<String> disposed = new ArrayList<>();
        RuntimeException failure = new RuntimeException("Cannot dispose texture");
        GameSession session = new GameSession(null, null, null, List.of(
                () -> disposed.add("map"),
                () -> { disposed.add("texture"); throw failure; }));

        try {
            session.dispose();
            fail("Expected disposal failure");
        } catch (RuntimeException actual) {
            assertSame(failure, actual);
        }
        session.dispose();

        assertEquals(List.of("texture", "map"), disposed);
    }
}

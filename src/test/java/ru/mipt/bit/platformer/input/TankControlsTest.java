package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.input.ai.RandomTankController;
import ru.mipt.bit.platformer.input.command.MoveCommand;
import ru.mipt.bit.platformer.input.keyboard.TankKeyboardController;
import ru.mipt.bit.platformer.input.keyboard.TestInput;
import ru.mipt.bit.platformer.model.bullet.Bullet;
import ru.mipt.bit.platformer.model.level.LevelBounds;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;
import ru.mipt.bit.platformer.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.SPACE;
import static com.badlogic.gdx.Input.Keys.UP;
import static org.junit.Assert.*;

public class TankControlsTest {
    @Test public void playerMovesFirstAndAiWaitsUntilContestedCellIsReleased() {
        Tank player = new Tank(0, 0);
        Tank computer = new Tank(2, 0);
        GameWorld world = new GameWorld(List.of(player, computer),
                new LevelBounds(3, 2), List.of(player, computer));
        TestInput input = new TestInput();
        Random random = new Random() {
            @Override
            public int nextInt(int bound) {
                return Direction.LEFT.ordinal();
            }
        };
        TankControls controls = new TankControls();
        controls.addStrategy(new TankKeyboardController(input.input,
                direction -> new MoveCommand(player, direction, world::canMoveTo).run()));
        controls.addStrategy(new RandomTankController(computer, world::canMoveTo, random, world::add,
                world::getElapsedSeconds));

        input.held.add(RIGHT);
        controls.controlTanks();
        assertEquals(new GridPoint2(1, 0), player.getDestinationTileCoordinates());
        assertFalse(computer.isMoving());
        world.update(2f);

        input.held.clear();
        input.held.add(UP);
        controls.controlTanks();
        assertEquals(new GridPoint2(1, 1), player.getDestinationTileCoordinates());
        assertFalse(computer.isMoving());
        world.update(2f);

        input.held.clear();
        controls.controlTanks();
        world.update(0.4f);
        assertEquals(new GridPoint2(1, 1), player.getTileCoordinates());
        assertEquals(new GridPoint2(1, 0), computer.getTileCoordinates());
    }

    @Test public void customStrategiesCollectBeforeCommandsExecute() {
        TankControls controls = new TankControls();
        List<String> events = new ArrayList<>();
        controls.addStrategy(submit -> {
            events.add("collect first");
            submit.accept(() -> events.add("execute first"));
        });
        controls.addStrategy(submit -> {
            events.add("collect second");
            submit.accept(() -> events.add("execute second"));
        });

        controls.controlTanks();
        assertEquals(List.of("collect first", "collect second", "execute first", "execute second"), events);
    }

    @Test public void keyboardAndShortFormAiDeferTheirActionsUntilEveryStrategyHasChosen() {
        Tank player = new Tank(0, 0);
        Tank enemy = new Tank(0, 1);
        List<Bullet> shots = new ArrayList<>();
        TestInput input = new TestInput();
        input.held.add(RIGHT);
        input.justPressed.add(SPACE);
        Random chooseRight = new Random() {
            @Override public int nextInt(int bound) { return Direction.RIGHT.ordinal(); }
        };
        TankControls controls = new TankControls();
        controls.addStrategy(new TankKeyboardController(input.input,
                direction -> player.tryMove(direction, tile -> true), () -> player.shoot(shots::add)));
        controls.addStrategy(new RandomTankController(enemy, tile -> true, chooseRight, shots::add));
        controls.addStrategy(submit -> {
            assertFalse(player.isMoving());
            assertFalse(enemy.isMoving());
            assertTrue(shots.isEmpty());
        });

        controls.controlTanks();

        assertTrue(player.isMoving());
        assertTrue(enemy.isMoving());
        assertEquals(1, shots.size());
    }

    @Test public void submissionsBeforeAndDuringAFrameRunOnceInTheirRespectiveFrames() {
        List<String> events = new ArrayList<>();
        TankControls controls = new TankControls();
        controls.submit(() -> {
            events.add("before frame");
            controls.submit(() -> events.add("next frame"));
        });
        controls.addStrategy(submit -> {
            events.add("collect");
            submit.accept(() -> events.add("execute"));
        });

        controls.controlTanks();
        assertEquals(List.of("collect", "before frame", "execute"), events);
        events.clear();
        controls.controlTanks();
        assertEquals(List.of("collect", "next frame", "execute"), events);
    }

    @Test public void failedSelectionDoesNotReplayPartialCommandsOnTheNextFrame() {
        TankControls controls = new TankControls();
        List<String> executed = new ArrayList<>();
        AtomicBoolean firstPoll = new AtomicBoolean(true);
        IllegalStateException failure = new IllegalStateException("Input unavailable");
        controls.addStrategy(submit -> submit.accept(() -> executed.add("move")));
        controls.addStrategy(submit -> {
            if (firstPoll.getAndSet(false)) {
                throw failure;
            }
        });

        assertSame(failure, assertThrows(IllegalStateException.class, controls::controlTanks));
        assertTrue(executed.isEmpty());
        controls.controlTanks();
        assertEquals(List.of("move"), executed);
    }
}

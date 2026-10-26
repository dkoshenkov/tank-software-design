package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.input.keyboard.TestInput;
import ru.mipt.bit.platformer.model.level.LevelBounds;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;
import ru.mipt.bit.platformer.world.GameWorld;

import java.util.List;
import java.util.Random;

import static com.badlogic.gdx.Input.Keys.RIGHT;
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
        TankControls controls = new TankControls(
                input.input, player, List.of(computer), world::canMoveTo, random);

        input.held.add(RIGHT);
        controls.controlTanks();
        assertEquals(new GridPoint2(1, 0), player.getDestinationTileCoordinates());
        assertFalse(computer.isMoving());
        world.update(0.4f);

        input.held.clear();
        input.held.add(UP);
        controls.controlTanks();
        assertEquals(new GridPoint2(1, 1), player.getDestinationTileCoordinates());
        assertFalse(computer.isMoving());
        world.update(0.4f);

        input.held.clear();
        controls.controlTanks();
        world.update(0.4f);
        assertEquals(new GridPoint2(1, 1), player.getTileCoordinates());
        assertEquals(new GridPoint2(1, 0), computer.getTileCoordinates());
    }
}

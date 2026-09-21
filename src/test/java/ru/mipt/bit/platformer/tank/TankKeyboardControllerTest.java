package ru.mipt.bit.platformer.tank;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.input.TestInput;
import static com.badlogic.gdx.Input.Keys.*;
import static org.junit.Assert.*;

public class TankKeyboardControllerTest {
    @Test public void allArrowAndWasdKeysMoveInExpectedDirections() {
        int[] keys = {UP, W, LEFT, A, DOWN, S, RIGHT, D};
        int[][] destinations = {{2,3}, {2,3}, {1,2}, {1,2}, {2,1}, {2,1}, {3,2}, {3,2}};
        for (int i = 0; i < keys.length; i++) {
            Tank tank = new Tank(2, 2);
            TestInput input = new TestInput();
            input.held.add(keys[i]);
            new TankKeyboardController(tank, tile -> true).handleInput(input.input);
            assertEquals("Key " + keys[i], new GridPoint2(destinations[i][0], destinations[i][1]),
                    tank.getDestinationTileCoordinates());
        }
    }

    @Test public void noInputLeavesTankIdle() {
        Tank tank = new Tank(2, 2);
        new TankKeyboardController(tank, tile -> true).handleInput(new TestInput().input);
        assertEquals(new GridPoint2(2, 2), tank.getTileCoordinates());
        assertEquals(new GridPoint2(2, 2), tank.getDestinationTileCoordinates());
        assertEquals(1f, tank.getMovementProgress(), 0f);
    }

    @Test public void simultaneousDirectionsKeepExistingPriority() {
        Tank tank = new Tank(2, 2);
        TestInput input = new TestInput();
        input.held.add(W);
        input.held.add(D);
        new TankKeyboardController(tank, tile -> true).handleInput(input.input);
        assertEquals(new GridPoint2(2, 3), tank.getDestinationTileCoordinates());
    }

    @Test public void blockedFirstDirectionAllowsNextPressedDirection() {
        Tank tank = new Tank(2, 2);
        TestInput input = new TestInput();
        input.held.add(W);
        input.held.add(D);
        new TankKeyboardController(tank, tile -> tile.y != 3).handleInput(input.input);
        assertEquals(new GridPoint2(3, 2), tank.getDestinationTileCoordinates());
    }

}

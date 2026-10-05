package ru.mipt.bit.platformer.tank;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.input.TestInput;
import java.util.ArrayList;
import java.util.List;
import static com.badlogic.gdx.Input.Keys.*;
import static org.junit.Assert.*;

public class TankKeyboardControllerTest {
    @Test public void allArrowAndWasdKeysMoveInExpectedDirections() {
        int[] keys = {UP, W, LEFT, A, DOWN, S, RIGHT, D};
        for (int i = 0; i < keys.length; i++) {
            TestInput input = new TestInput();
            input.held.add(keys[i]);
            List<Direction> directions = new ArrayList<>();
            new TankKeyboardController(directions::add).handleInput(input.input);
            assertEquals("Key " + keys[i],
                    Direction.values()[i / 2], directions.get(0));
        }
    }

    @Test public void noInputLeavesTankIdle() {
        List<Direction> directions = new ArrayList<>();
        new TankKeyboardController(directions::add).handleInput(new TestInput().input);
        assertTrue(directions.isEmpty());
    }

    @Test public void heldDirectionsKeepUpLeftDownRightOrder() {
        TestInput input = new TestInput();
        input.held.add(UP);
        input.held.add(LEFT);
        input.held.add(DOWN);
        input.held.add(RIGHT);
        List<Direction> directions = new ArrayList<>();

        new TankKeyboardController(directions::add).handleInput(input.input);

        assertEquals(List.of(Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT), directions);
    }

    @Test public void blockedFirstDirectionAllowsNextPressedDirection() {
        Tank tank = new Tank(2, 2);
        TestInput input = new TestInput();
        input.held.add(W);
        input.held.add(D);
        TankKeyboardController controller = new TankKeyboardController(direction ->
                tank.tryMove(direction, tile -> tile.y != 3));
        controller.handleInput(input.input);
        assertEquals(new GridPoint2(3, 2), tank.getDestinationTileCoordinates());
    }

}

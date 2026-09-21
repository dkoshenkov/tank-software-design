package ru.mipt.bit.platformer.input;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.badlogic.gdx.Input.Keys.*;
import static org.junit.Assert.*;

public class KeyboardControllerTest {
    @Test public void heldActionRepeatsOncePerFrameEvenWithBothAliases() {
        KeyboardController controller = new KeyboardController();
        TestInput input = new TestInput();
        List<String> calls = new ArrayList<>();
        controller.bindHeld(() -> calls.add("move"), W, UP);
        controller.handleInput(input.input);
        assertTrue(calls.isEmpty());
        input.held.add(W);
        input.held.add(UP);
        controller.handleInput(input.input);
        controller.handleInput(input.input);
        assertEquals(Arrays.asList("move", "move"), calls);
    }

    @Test public void justPressedActionDoesNotRepeatWhileHeldAndCanBePressedAgain() {
        KeyboardController controller = new KeyboardController();
        TestInput input = new TestInput();
        List<String> calls = new ArrayList<>();
        controller.bindHeld(() -> calls.add("move"), W);
        controller.bindJustPressed(() -> calls.add("fire"), SPACE);
        input.held.add(W);
        input.held.add(SPACE);
        input.justPressed.add(SPACE);
        controller.handleInput(input.input);
        input.justPressed.clear();
        controller.handleInput(input.input);
        input.justPressed.add(SPACE);
        controller.handleInput(input.input);
        assertEquals(Arrays.asList("move", "fire", "move", "move", "fire"), calls);
    }

    @Test public void bindingCopiesKeys() {
        KeyboardController controller = new KeyboardController();
        TestInput input = new TestInput();
        int[] keys = {SPACE};
        int[] calls = {0};
        controller.bindJustPressed(() -> calls[0]++, keys);
        keys[0] = W;
        input.justPressed.add(SPACE);
        controller.handleInput(input.input);
        assertEquals(1, calls[0]);
    }
}

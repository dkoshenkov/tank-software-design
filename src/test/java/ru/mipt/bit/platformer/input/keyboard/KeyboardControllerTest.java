package ru.mipt.bit.platformer.input.keyboard;

import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static com.badlogic.gdx.Input.Keys.*;
import static org.junit.Assert.*;

public class KeyboardControllerTest {
    @Test public void justPressedActionDoesNotRepeatWhileHeld() {
        KeyboardController controller = new KeyboardController();
        TestInput input = new TestInput();
        List<String> calls = new ArrayList<>();
        controller.bindJustPressed(() -> calls.add("toggle"), L, H);
        controller.handleInput(input.input);
        input.held.add(L);
        controller.handleInput(input.input);
        assertTrue(calls.isEmpty());

        input.justPressed.add(L);
        input.justPressed.add(H);
        controller.handleInput(input.input);
        assertEquals(List.of("toggle"), calls);
        input.justPressed.clear();
        controller.handleInput(input.input);
        assertEquals(List.of("toggle"), calls);
        input.justPressed.add(L);
        controller.handleInput(input.input);
        assertEquals(List.of("toggle", "toggle"), calls);
    }

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
        assertEquals(List.of("move", "move"), calls);
    }

    @Test public void bindingCopiesKeys() {
        KeyboardController controller = new KeyboardController();
        TestInput input = new TestInput();
        int[] keys = {SPACE};
        int[] calls = {0};
        controller.bindHeld(() -> calls[0]++, keys);
        keys[0] = W;
        input.held.add(SPACE);
        controller.handleInput(input.input);
        assertEquals(1, calls[0]);
    }
}

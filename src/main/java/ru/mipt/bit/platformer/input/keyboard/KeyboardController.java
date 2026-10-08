package ru.mipt.bit.platformer.input.keyboard;

import com.badlogic.gdx.Input;
import java.util.ArrayList;
import java.util.List;

public class KeyboardController {
    private final List<Binding> bindings = new ArrayList<>();

    public void bindHeld(Runnable action, int... keys) {
        bindings.add(new Binding(action, keys));
    }

    public void handleInput(Input input) {
        for (Binding binding : bindings) {
            if (isAnyKeyPressed(input, binding.keys)) {
                binding.action.run();
            }
        }
    }

    private boolean isAnyKeyPressed(Input input, int[] keys) {
        for (int key : keys) {
            if (input.isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }

    private static final class Binding {
        private final Runnable action;
        private final int[] keys;

        private Binding(Runnable action, int[] keys) {
            this.action = action;
            this.keys = keys.clone();
        }
    }
}

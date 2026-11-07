package ru.mipt.bit.platformer.input.keyboard;

import com.badlogic.gdx.Input;
import java.util.ArrayList;
import java.util.List;

public class KeyboardController {
    private final List<Binding> bindings = new ArrayList<>();

    public void bindHeld(Runnable action, int... keys) {
        bindings.add(new Binding(action, keys, false));
    }

    public void bindJustPressed(Runnable action, int... keys) {
        bindings.add(new Binding(action, keys, true));
    }

    public void handleInput(Input input) {
        for (Binding binding : bindings) {
            if (isAnyKeyPressed(input, binding)) {
                binding.action.run();
            }
        }
    }

    private boolean isAnyKeyPressed(Input input, Binding binding) {
        for (int key : binding.keys) {
            if (binding.justPressed ? input.isKeyJustPressed(key) : input.isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }

    private static final class Binding {
        private final Runnable action;
        private final int[] keys;
        private final boolean justPressed;

        private Binding(Runnable action, int[] keys, boolean justPressed) {
            this.action = action;
            this.keys = keys.clone();
            this.justPressed = justPressed;
        }
    }
}

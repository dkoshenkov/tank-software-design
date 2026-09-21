package ru.mipt.bit.platformer.input;

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
            for (int key : binding.keys) {
                if (input.isKeyPressed(key)) {
                    binding.action.run();
                    break;
                }
            }
        }
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

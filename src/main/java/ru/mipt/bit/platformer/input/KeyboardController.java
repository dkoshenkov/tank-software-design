package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Input;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

public class KeyboardController {
    private final List<Binding> bindings = new ArrayList<>();

    public void bindHeld(Runnable action, int... keys) {
        bindings.add(new Binding(action, false, keys));
    }

    public void bindJustPressed(Runnable action, int... keys) {
        bindings.add(new Binding(action, true, keys));
    }

    public void handleInput(Input input) {
        for (Binding binding : bindings) {
            IntPredicate isPressed = binding.justPressed ? input::isKeyJustPressed : input::isKeyPressed;
            for (int key : binding.keys) {
                if (isPressed.test(key)) {
                    binding.action.run();
                    break;
                }
            }
        }
    }

    private static final class Binding {
        private final Runnable action;
        private final boolean justPressed;
        private final int[] keys;

        private Binding(Runnable action, boolean justPressed, int[] keys) {
            this.action = action;
            this.justPressed = justPressed;
            this.keys = keys.clone();
        }
    }
}

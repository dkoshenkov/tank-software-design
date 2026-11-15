package ru.mipt.bit.platformer.input.keyboard;

import com.badlogic.gdx.Input;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Адаптер клавиатуры. Передаёт команду, когда игрок нажимает или удерживает клавишу.
 */
public class KeyboardController {
    private final List<Binding> bindings = new ArrayList<>();

    public void bindHeld(Runnable action, int... keys) {
        bindings.add(new Binding(action, keys, false));
    }

    public void bindJustPressed(Runnable action, int... keys) {
        bindings.add(new Binding(action, keys, true));
    }

    public void handleInput(Input input) {
        handleInput(input, Runnable::run);
    }

    public void handleInput(Input input, Consumer<Runnable> submit) {
        for (Binding binding : bindings) {
            if (isAnyKeyPressed(input, binding)) {
                submit.accept(binding.action);
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

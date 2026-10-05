package ru.mipt.bit.platformer.tank;

import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.input.KeyboardController;
import java.util.function.Consumer;

import static com.badlogic.gdx.Input.Keys.*;

public class TankKeyboardController {
    private final KeyboardController keyboardController = new KeyboardController();

    public TankKeyboardController(Consumer<Direction> move) {
        keyboardController.bindHeld(() -> move.accept(Direction.UP), UP, W);
        keyboardController.bindHeld(() -> move.accept(Direction.LEFT), LEFT, A);
        keyboardController.bindHeld(() -> move.accept(Direction.DOWN), DOWN, S);
        keyboardController.bindHeld(() -> move.accept(Direction.RIGHT), RIGHT, D);
    }

    public void handleInput(Input input) {
        keyboardController.handleInput(input);
    }
}

package ru.mipt.bit.platformer.input.keyboard;

import ru.mipt.bit.platformer.model.tank.Direction;
import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.input.TankControlStrategy;
import java.util.function.Consumer;

import static com.badlogic.gdx.Input.Keys.*;

public class TankKeyboardController implements TankControlStrategy {
    private final Input input;
    private final KeyboardController keyboardController = new KeyboardController();

    public TankKeyboardController(Input input, Consumer<Direction> move) {
        this.input = input;
        keyboardController.bindHeld(() -> move.accept(Direction.UP), UP, W);
        keyboardController.bindHeld(() -> move.accept(Direction.LEFT), LEFT, A);
        keyboardController.bindHeld(() -> move.accept(Direction.DOWN), DOWN, S);
        keyboardController.bindHeld(() -> move.accept(Direction.RIGHT), RIGHT, D);
    }

    @Override
    public void controlTank() {
        keyboardController.handleInput(input);
    }
}

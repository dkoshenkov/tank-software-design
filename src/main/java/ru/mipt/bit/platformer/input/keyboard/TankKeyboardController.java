package ru.mipt.bit.platformer.input.keyboard;

import ru.mipt.bit.platformer.model.tank.Direction;
import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.input.TankControlStrategy;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.badlogic.gdx.Input.Keys.*;

/**
 * Адаптер {@link TankControlStrategy}. По нажатию клавиш даёт танку команду ехать или стрелять.
 */
public class TankKeyboardController implements TankControlStrategy {
    private final Input input;
    private final KeyboardController keyboardController = new KeyboardController();

    public TankKeyboardController(Input input, Consumer<Direction> move) {
        this(input, move, () -> { });
    }

    public TankKeyboardController(Input input, Consumer<Direction> move, Runnable shoot) {
        this(input);
        bindCommands(direction -> () -> move.accept(direction), shoot);
    }

    public static TankKeyboardController withCommands(Input input, Function<Direction, Runnable> move, Runnable shoot) {
        TankKeyboardController controller = new TankKeyboardController(input);
        controller.bindCommands(move, shoot);
        return controller;
    }

    private TankKeyboardController(Input input) {
        this.input = input;
    }

    private void bindCommands(Function<Direction, Runnable> move, Runnable shoot) {
        keyboardController.bindHeld(move.apply(Direction.UP), UP, W);
        keyboardController.bindHeld(move.apply(Direction.LEFT), LEFT, A);
        keyboardController.bindHeld(move.apply(Direction.DOWN), DOWN, S);
        keyboardController.bindHeld(move.apply(Direction.RIGHT), RIGHT, D);
        keyboardController.bindJustPressed(shoot, SPACE);
    }

    @Override
    public void collectCommands(Consumer<Runnable> submit) {
        keyboardController.handleInput(input, submit);
    }
}

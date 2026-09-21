package ru.mipt.bit.platformer.tank;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.input.KeyboardController;
import java.util.function.Predicate;

import static com.badlogic.gdx.Input.Keys.*;

public class TankKeyboardController extends KeyboardController {
    public TankKeyboardController(Tank tank, Predicate<GridPoint2> canMoveTo) {
        bindHeld(() -> tank.tryMove(Direction.UP, canMoveTo), UP, W);
        bindHeld(() -> tank.tryMove(Direction.LEFT, canMoveTo), LEFT, A);
        bindHeld(() -> tank.tryMove(Direction.DOWN, canMoveTo), DOWN, S);
        bindHeld(() -> tank.tryMove(Direction.RIGHT, canMoveTo), RIGHT, D);
    }
}

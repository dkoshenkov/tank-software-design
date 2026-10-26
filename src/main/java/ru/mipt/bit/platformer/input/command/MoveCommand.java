package ru.mipt.bit.platformer.input.command;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.util.function.Predicate;

public final class MoveCommand implements Runnable {
    private final Tank tank;
    private final Direction direction;
    private final Predicate<GridPoint2> canMoveTo;

    public MoveCommand(Tank tank, Direction direction, Predicate<GridPoint2> canMoveTo) {
        this.tank = tank;
        this.direction = direction;
        this.canMoveTo = canMoveTo;
    }

    @Override
    public void run() {
        tank.tryMove(direction, canMoveTo);
    }
}

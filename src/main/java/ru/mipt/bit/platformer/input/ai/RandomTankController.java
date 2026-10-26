package ru.mipt.bit.platformer.input.ai;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.input.TankControlStrategy;
import ru.mipt.bit.platformer.input.command.MoveCommand;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.util.Random;
import java.util.function.Predicate;

public final class RandomTankController implements TankControlStrategy {
    private final Tank tank;
    private final Random random;
    private final MoveCommand[] commands;

    public RandomTankController(Tank tank, Predicate<GridPoint2> canMoveTo, Random random) {
        this.tank = tank;
        this.random = random;
        commands = createCommands(tank, canMoveTo);
    }

    private MoveCommand[] createCommands(Tank tank, Predicate<GridPoint2> canMoveTo) {
        Direction[] directions = Direction.values();
        MoveCommand[] commands = new MoveCommand[directions.length];
        for (int i = 0; i < directions.length; i++) {
            commands[i] = new MoveCommand(tank, directions[i], canMoveTo);
        }
        return commands;
    }

    @Override
    public void controlTank() {
        if (!tank.isMoving()) {
            commands[random.nextInt(commands.length)].run();
        }
    }
}

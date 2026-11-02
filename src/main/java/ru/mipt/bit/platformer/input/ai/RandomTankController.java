package ru.mipt.bit.platformer.input.ai;

import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.input.TankControlStrategy;
import ru.mipt.bit.platformer.input.command.MoveCommand;
import ru.mipt.bit.platformer.input.command.ShootCommand;
import ru.mipt.bit.platformer.model.bullet.Bullet;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class RandomTankController implements TankControlStrategy {
    private final Tank tank;
    private final Random random;
    private final Runnable[] commands;
    private final Consumer<Runnable> submit;

    public RandomTankController(Tank tank, Predicate<GridPoint2> canMoveTo, Random random,
                                Consumer<Bullet> registerBullet) {
        this(tank, canMoveTo, random, registerBullet, Runnable::run);
    }

    public RandomTankController(Tank tank, Predicate<GridPoint2> canMoveTo, Random random,
                                Consumer<Bullet> registerBullet, Consumer<Runnable> submit) {
        this.tank = tank;
        this.random = random;
        this.submit = submit;
        commands = createCommands(tank, canMoveTo, registerBullet);
    }

    private Runnable[] createCommands(Tank tank, Predicate<GridPoint2> canMoveTo, Consumer<Bullet> registerBullet) {
        Direction[] directions = Direction.values();
        Runnable[] commands = new Runnable[directions.length + 1];
        for (int i = 0; i < directions.length; i++) {
            commands[i] = new MoveCommand(tank, directions[i], canMoveTo);
        }
        commands[directions.length] = new ShootCommand(tank, registerBullet);
        return commands;
    }

    @Override
    public void controlTank() {
        if (!tank.isDestroyed() && !tank.isMoving()) {
            submit.accept(commands[random.nextInt(commands.length)]);
        }
    }
}

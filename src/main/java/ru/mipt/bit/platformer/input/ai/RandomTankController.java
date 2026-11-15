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
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;

/**
 * Адаптер {@link TankControlStrategy}. Выбирает случайные команды для танка ИИ.
 */
public final class RandomTankController implements TankControlStrategy {
    private static final double MIN_DECISION_INTERVAL_SECONDS = 1.2;
    private static final double MAX_DECISION_INTERVAL_SECONDS = 1.8;
    private final Tank tank;
    private final Random random;
    private final Runnable[] commands;
    private final Consumer<Runnable> submit;
    private final DoubleSupplier timeSeconds;
    private double nextDecisionAt = Double.NEGATIVE_INFINITY;

    public RandomTankController(Tank tank, Predicate<GridPoint2> canMoveTo, Random random,
                                Consumer<Bullet> registerBullet) {
        this(tank, canMoveTo, random, registerBullet, Runnable::run);
    }

    public RandomTankController(Tank tank, Predicate<GridPoint2> canMoveTo, Random random,
                                Consumer<Bullet> registerBullet, Consumer<Runnable> submit) {
        this(tank, canMoveTo, random, registerBullet, submit, () -> System.nanoTime() / 1_000_000_000.0);
    }

    public RandomTankController(Tank tank, Predicate<GridPoint2> canMoveTo, Random random,
                                Consumer<Bullet> registerBullet, DoubleSupplier timeSeconds) {
        this(tank, canMoveTo, random, registerBullet, Runnable::run, timeSeconds);
    }

    public RandomTankController(Tank tank, Predicate<GridPoint2> canMoveTo, Random random,
                                Consumer<Bullet> registerBullet, Consumer<Runnable> submit,
                                DoubleSupplier timeSeconds) {
        this.tank = tank;
        this.random = random;
        this.submit = submit;
        this.timeSeconds = timeSeconds;
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
        collectCommands(submit);
    }

    @Override
    public void collectCommands(Consumer<Runnable> submit) {
        double now = timeSeconds.getAsDouble();
        if (canDecide(now)) {
            scheduleNextDecision(now);
            submit.accept(commands[random.nextInt(commands.length)]);
        }
    }

    private boolean canDecide(double now) {
        return !tank.isDestroyed() && !tank.isMoving() && now >= nextDecisionAt;
    }

    private void scheduleNextDecision(double now) {
        nextDecisionAt = now + MIN_DECISION_INTERVAL_SECONDS
                + random.nextDouble() * (MAX_DECISION_INTERVAL_SECONDS - MIN_DECISION_INTERVAL_SECONDS);
    }
}

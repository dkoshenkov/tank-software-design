package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.input.ai.RandomTankController;
import ru.mipt.bit.platformer.input.command.MoveCommand;
import ru.mipt.bit.platformer.input.command.ShootCommand;
import ru.mipt.bit.platformer.input.keyboard.TankKeyboardController;
import ru.mipt.bit.platformer.model.bullet.Bullet;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class TankControls {
    private final List<TankControlStrategy> strategies = new ArrayList<>();
    private final List<Runnable> commands = new ArrayList<>();

    public TankControls(Input input, Tank player, List<Tank> computerTanks,
                        Predicate<GridPoint2> canMoveTo, Random random, Consumer<Bullet> registerBullet) {
        strategies.add(new TankKeyboardController(input, direction -> {
            if (!player.isDestroyed()) {
                commands.add(new MoveCommand(player, direction, canMoveTo));
            }
        }, () -> {
            if (!player.isDestroyed()) {
                commands.add(new ShootCommand(player, registerBullet));
            }
        }));
        for (Tank tank : computerTanks) {
            strategies.add(new RandomTankController(tank, canMoveTo, random, registerBullet, commands::add));
        }
    }

    public void controlTanks() {
        commands.clear();
        for (TankControlStrategy strategy : strategies) {
            strategy.controlTank();
        }
        for (Runnable command : commands) {
            command.run();
        }
        commands.clear();
    }
}

package ru.mipt.bit.platformer.input.command;

import ru.mipt.bit.platformer.model.bullet.Bullet;
import ru.mipt.bit.platformer.model.tank.Tank;
import java.util.function.Consumer;

public final class ShootCommand implements Runnable {
    private final Tank tank;
    private final Consumer<Bullet> registerBullet;

    public ShootCommand(Tank tank, Consumer<Bullet> registerBullet) {
        this.tank = tank;
        this.registerBullet = registerBullet;
    }

    @Override
    public void run() {
        tank.shoot(registerBullet);
    }
}

package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.TileBlocker;
import ru.mipt.bit.platformer.model.TilePosition;
import ru.mipt.bit.platformer.model.bullet.Bullet;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CombatSystem {
    private final GameWorld world;

    public CombatSystem(GameWorld world) {
        this.world = world;
        world.configure(this::accepts, this::objectAdded, this::update);
        removeDestroyedTanks();
        resolveBulletCollisions();
    }

    private boolean accepts(TilePosition object) {
        return !(object instanceof Tank) || !((Tank) object).isDestroyed();
    }

    private void objectAdded(TilePosition object) {
        if (object instanceof Bullet) {
            resolveCollision((Bullet) object);
        }
    }

    private void removeDestroyedTanks() {
        for (TilePosition object : world.objects()) {
            if (object instanceof Tank && ((Tank) object).isDestroyed()) {
                world.remove(object);
            }
        }
    }

    private void update(float deltaSeconds) {
        removeDestroyedTanks();
        advanceObjects(deltaSeconds);
    }

    private List<Bullet> bullets() {
        List<Bullet> bullets = new ArrayList<>();
        for (TilePosition object : world.objects()) {
            if (object instanceof Bullet) {
                bullets.add((Bullet) object);
            }
        }
        return bullets;
    }

    private void advanceObjects(double remainingSeconds) {
        resolveBulletCollisions();
        do {
            List<Bullet> bullets = bullets();
            double step = remainingSeconds;
            for (Bullet bullet : bullets) {
                step = Math.min(step, bullet.secondsUntilNextTile());
            }
            world.updateUpdatables((float) step);
            Map<Bullet, GridPoint2> previousPositions = new LinkedHashMap<>();
            for (Bullet bullet : bullets) {
                previousPositions.put(bullet, bullet.getTileCoordinates());
                bullet.update(step);
            }
            removeCrossingBullets(previousPositions);
            resolveBulletCollisions();
            remainingSeconds -= step;
        } while (remainingSeconds > 0);
    }

    private void removeCrossingBullets(Map<Bullet, GridPoint2> previousPositions) {
        for (Map.Entry<Bullet, GridPoint2> first : previousPositions.entrySet()) {
            for (Map.Entry<Bullet, GridPoint2> second : previousPositions.entrySet()) {
                if (first.getKey() != second.getKey()
                        && !first.getValue().equals(first.getKey().getTileCoordinates())
                        && first.getValue().equals(second.getKey().getTileCoordinates())
                        && second.getValue().equals(first.getKey().getTileCoordinates())) {
                    world.remove(first.getKey());
                    world.remove(second.getKey());
                }
            }
        }
    }

    private void resolveBulletCollisions() {
        for (Bullet bullet : bullets()) {
            if (world.contains(bullet)) {
                resolveCollision(bullet);
            }
        }
    }

    private void resolveCollision(Bullet bullet) {
        GridPoint2 tile = bullet.getTileCoordinates();
        if (!world.containsTile(tile)) {
            world.remove(bullet);
            return;
        }
        boolean collided = false;
        for (TileBlocker obstacle : world.blockers()) {
            if (obstacle instanceof Tank && bullet.isOwnedBy((Tank) obstacle)) {
                continue;
            }
            if (obstacle.occupiesTile(tile)) {
                collided = true;
                if (obstacle instanceof Tank) {
                    Tank tank = (Tank) obstacle;
                    tank.takeDamage(bullet.getDamage());
                    if (tank.isDestroyed()) {
                        world.remove(tank);
                    }
                }
            }
        }
        for (TilePosition object : world.objects()) {
            if (object instanceof Bullet && object != bullet && object.getTileCoordinates().equals(tile)) {
                collided = true;
                world.remove(object);
            }
        }
        if (collided) {
            world.remove(bullet);
        }
    }

}

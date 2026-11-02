package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.model.TileBlocker;
import ru.mipt.bit.platformer.model.TileBounds;
import ru.mipt.bit.platformer.model.TilePosition;
import ru.mipt.bit.platformer.model.Updatable;
import ru.mipt.bit.platformer.model.bullet.Bullet;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameWorld {
    private final List<Updatable> updatables = new ArrayList<>();
    private final TileBounds tileBounds;
    private final List<TileBlocker> obstacles = new ArrayList<>();
    private final List<TilePosition> objects = new ArrayList<>();
    private final List<WorldListener> listeners = new ArrayList<>();

    public GameWorld(TileBounds tileBounds) {
        this.tileBounds = tileBounds;
    }

    public GameWorld(List<Updatable> updatables, TileBounds tileBounds, List<TileBlocker> obstacles) {
        this(tileBounds);
        this.updatables.addAll(updatables);
        this.obstacles.addAll(obstacles);
        for (Updatable object : updatables) {
            if (object instanceof TilePosition && !objects.contains(object)) {
                objects.add((TilePosition) object);
            }
        }
        for (TileBlocker object : obstacles) {
            if (object instanceof TilePosition && !objects.contains(object)) {
                objects.add((TilePosition) object);
            }
        }
    }

    public void addListener(WorldListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            for (TilePosition object : List.copyOf(objects)) {
                listener.objectAdded(object);
            }
        }
    }

    public void removeListener(WorldListener listener) {
        listeners.remove(listener);
    }

    public void add(TilePosition object) {
        if (objects.contains(object) || object instanceof Tank && ((Tank) object).isDestroyed()) {
            return;
        }
        objects.add(object);
        if (object instanceof Updatable) {
            updatables.add((Updatable) object);
        }
        if (object instanceof TileBlocker) {
            obstacles.add((TileBlocker) object);
        }
        for (WorldListener listener : List.copyOf(listeners)) {
            listener.objectAdded(object);
        }
        if (object instanceof Bullet) {
            resolveCollision((Bullet) object);
        }
    }

    public void remove(TilePosition object) {
        if (!objects.remove(object)) {
            return;
        }
        updatables.remove(object);
        obstacles.remove(object);
        for (WorldListener listener : List.copyOf(listeners)) {
            listener.objectRemoved(object);
        }
    }

    public void update(float deltaSeconds) {
        if (!Float.isFinite(deltaSeconds) || deltaSeconds < 0f) {
            throw new IllegalArgumentException("Delta must be finite and nonnegative");
        }
        for (TilePosition object : List.copyOf(objects)) {
            if (object instanceof Tank && ((Tank) object).isDestroyed()) {
                remove(object);
            }
        }
        advanceObjects(deltaSeconds);
    }

    private void updateUpdatables(float deltaSeconds) {
        for (Updatable updatable : List.copyOf(updatables)) {
            if (updatables.contains(updatable)) {
                updatable.update(deltaSeconds);
            }
        }
    }

    private List<Bullet> bullets() {
        List<Bullet> bullets = new ArrayList<>();
        for (TilePosition object : objects) {
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
            // Tanks and bullets must reach the same instant before checking hits.
            updateUpdatables((float) step);
            Map<Bullet, GridPoint2> previousPositions = new LinkedHashMap<>();
            for (Bullet bullet : bullets) {
                previousPositions.put(bullet, bullet.getTileCoordinates());
                bullet.update(step);
            }
            // Move every bullet to the same instant before testing collisions.
            // Otherwise a following bullet can hit the leader's obsolete position.
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
                    remove(first.getKey());
                    remove(second.getKey());
                }
            }
        }
    }

    private void resolveBulletCollisions() {
        for (Bullet bullet : bullets()) {
            if (objects.contains(bullet)) {
                resolveCollision(bullet);
            }
        }
    }

    private void resolveCollision(Bullet bullet) {
        GridPoint2 tile = bullet.getTileCoordinates();
        if (!tileBounds.containsTile(tile)) {
            remove(bullet);
            return;
        }
        boolean collided = false;
        for (TileBlocker obstacle : List.copyOf(obstacles)) {
            if (obstacle instanceof Tank && bullet.isOwnedBy((Tank) obstacle)) {
                continue;
            }
            if (obstacle.occupiesTile(tile)) {
                collided = true;
                if (obstacle instanceof Tank) {
                    Tank tank = (Tank) obstacle;
                    tank.takeDamage(bullet.getDamage());
                    if (tank.isDestroyed()) {
                        remove(tank);
                    }
                }
            }
        }
        for (TilePosition object : List.copyOf(objects)) {
            if (object instanceof Bullet && object != bullet && object.getTileCoordinates().equals(tile)) {
                collided = true;
                remove(object);
            }
        }
        if (collided) {
            remove(bullet);
        }
    }

    public boolean canMoveTo(GridPoint2 tileCoordinates) {
        return tileBounds.containsTile(tileCoordinates) && !isBlocked(tileCoordinates);
    }

    private boolean isBlocked(GridPoint2 tileCoordinates) {
        for (TileBlocker blocker : obstacles) {
            if (blocker.occupiesTile(tileCoordinates)) {
                return true;
            }
        }
        return false;
    }
}

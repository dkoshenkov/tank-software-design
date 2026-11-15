package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.TileBlocker;
import ru.mipt.bit.platformer.model.TilePosition;
import ru.mipt.bit.platformer.model.bullet.Bullet;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            resolveBulletCollisions(List.of((Bullet) object));
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
            double step = nextStep(bullets, remainingSeconds);
            advanceStep(bullets, step);
            remainingSeconds -= step;
        } while (remainingSeconds > 0);
    }

    private double nextStep(List<Bullet> bullets, double remainingSeconds) {
        double step = remainingSeconds;
        for (Bullet bullet : bullets) {
            step = Math.min(step, bullet.secondsUntilNextTile());
        }
        return step;
    }

    private void advanceStep(List<Bullet> bullets, double step) {
        world.updateUpdatables((float) step);
        Map<Bullet, GridPoint2> previousPositions = advanceBullets(bullets, step);
        for (Bullet bullet : crossingBullets(previousPositions)) {
            world.remove(bullet);
        }
        resolveBulletCollisions();
    }

    private Map<Bullet, GridPoint2> advanceBullets(List<Bullet> bullets, double step) {
        Map<Bullet, GridPoint2> previousPositions = new LinkedHashMap<>();
        for (Bullet bullet : bullets) {
            previousPositions.put(bullet, bullet.getTileCoordinates());
            bullet.update(step);
        }
        return previousPositions;
    }

    private Set<Bullet> crossingBullets(Map<Bullet, GridPoint2> previousPositions) {
        Set<Bullet> crossing = new LinkedHashSet<>();
        for (Map.Entry<Bullet, GridPoint2> first : previousPositions.entrySet()) {
            for (Map.Entry<Bullet, GridPoint2> second : previousPositions.entrySet()) {
                if (crossed(first, second)) {
                    crossing.add(first.getKey());
                    crossing.add(second.getKey());
                }
            }
        }
        return crossing;
    }

    private boolean crossed(Map.Entry<Bullet, GridPoint2> first, Map.Entry<Bullet, GridPoint2> second) {
        if (first.getKey() == second.getKey()) {
            return false;
        }
        GridPoint2 firstOrigin = first.getValue();
        GridPoint2 firstCurrent = first.getKey().getTileCoordinates();
        if (firstOrigin.equals(firstCurrent)) {
            return false;
        }
        GridPoint2 secondCurrent = second.getKey().getTileCoordinates();
        return firstOrigin.equals(secondCurrent) && second.getValue().equals(firstCurrent);
    }

    private void resolveBulletCollisions() {
        resolveBulletCollisions(bullets());
    }

    private void resolveBulletCollisions(List<Bullet> bullets) {
        Set<Bullet> removed = new LinkedHashSet<>();
        Map<Tank, List<Bullet>> hits = new LinkedHashMap<>();
        List<TileBlocker> blockers = world.blockers();
        List<TilePosition> objects = world.objects();
        for (Bullet bullet : bullets) {
            recordContacts(bullet, blockers, objects, hits, removed);
        }
        applyCollisions(hits, removed);
    }

    private void recordContacts(Bullet bullet, List<TileBlocker> blockers, List<TilePosition> objects,
                                Map<Tank, List<Bullet>> hits, Set<Bullet> removed) {
        if (!world.contains(bullet)) {
            return;
        }
        GridPoint2 tile = bullet.getTileCoordinates();
        if (!world.containsTile(tile)) {
            removed.add(bullet);
            return;
        }
        recordBlockerContacts(bullet, tile, blockers, hits, removed);
        recordBulletContacts(bullet, tile, objects, removed);
    }

    private void recordBlockerContacts(Bullet bullet, GridPoint2 tile, List<TileBlocker> blockers,
                                       Map<Tank, List<Bullet>> hits, Set<Bullet> removed) {
        for (TileBlocker obstacle : blockers) {
            if (obstacle instanceof Tank && bullet.isOwnedBy((Tank) obstacle)) {
                continue;
            }
            if (!obstacle.occupiesTile(tile)) {
                continue;
            }
            removed.add(bullet);
            if (obstacle instanceof Tank) {
                hits.computeIfAbsent((Tank) obstacle, ignored -> new ArrayList<>()).add(bullet);
            }
        }
    }

    private void recordBulletContacts(Bullet bullet, GridPoint2 tile, List<TilePosition> objects,
                                      Set<Bullet> removed) {
        for (TilePosition object : objects) {
            if (object instanceof Bullet && object != bullet && object.getTileCoordinates().equals(tile)) {
                removed.add(bullet);
                removed.add((Bullet) object);
            }
        }
    }

    private void applyCollisions(Map<Tank, List<Bullet>> hits, Set<Bullet> removed) {
        for (Map.Entry<Tank, List<Bullet>> hit : hits.entrySet()) {
            applyHits(hit.getKey(), hit.getValue());
        }
        for (Bullet bullet : removed) {
            world.remove(bullet);
        }
    }

    private void applyHits(Tank tank, List<Bullet> bullets) {
        for (Bullet bullet : bullets) {
            tank.takeDamage(bullet.getDamage());
        }
        if (tank.isDestroyed()) {
            world.remove(tank);
        }
    }

}

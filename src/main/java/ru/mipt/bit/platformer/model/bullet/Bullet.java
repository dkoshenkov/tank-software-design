package ru.mipt.bit.platformer.model.bullet;

import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.model.TilePosition;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;

public final class Bullet implements TilePosition {
    private static final double SECONDS_PER_TILE = 0.1;
    private final Tank owner;
    private final Direction direction;
    private final int damage;
    private final GridPoint2 position;
    private double elapsed;

    public Bullet(Tank owner, GridPoint2 position, Direction direction, int damage) {
        if (damage <= 0) {
            throw new IllegalArgumentException("Bullet damage must be positive");
        }
        this.owner = owner;
        this.position = new GridPoint2(position);
        this.direction = direction;
        this.damage = damage;
    }

    public void update(double deltaSeconds) {
        if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0f) {
            throw new IllegalArgumentException("Delta must be finite and nonnegative");
        }
        elapsed += deltaSeconds;
        while (elapsed >= SECONDS_PER_TILE) {
            elapsed -= SECONDS_PER_TILE;
            position.set(direction.getAdjacentTileCoordinates(position));
        }
    }

    public double secondsUntilNextTile() {
        return SECONDS_PER_TILE - elapsed;
    }

    public boolean isOwnedBy(Tank tank) {
        return owner == tank;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public GridPoint2 getTileCoordinates() {
        return new GridPoint2(position);
    }
}

package ru.mipt.bit.platformer.model.tank;

import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.model.MotionState;
import ru.mipt.bit.platformer.model.TileBlocker;
import ru.mipt.bit.platformer.model.Updatable;
import ru.mipt.bit.platformer.model.bullet.Bullet;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.badlogic.gdx.math.MathUtils.clamp;

public class Tank implements Updatable, MotionState, TileBlocker {
    private static final float MOVEMENT_START = 0f;
    private static final float MOVEMENT_COMPLETE = 1f;
    private static final float DEFAULT_MOVE_DURATION_SECONDS = 0.4f;

    private int health;
    private final int bulletDamage;
    private final GridPoint2 tileCoordinates;
    private final GridPoint2 destinationTileCoordinates;
    private final float moveDurationSeconds;
    private float movementProgress = MOVEMENT_COMPLETE;
    private Direction direction = Direction.RIGHT;

    public Tank(int tileX, int tileY) {
        this(tileX, tileY, DEFAULT_MOVE_DURATION_SECONDS);
    }

    public Tank(int tileX, int tileY, float moveDurationSeconds) {
        this(tileX, tileY, moveDurationSeconds, 2);
    }

    public Tank(int tileX, int tileY, float moveDurationSeconds, int health) {
        this(tileX, tileY, moveDurationSeconds, health, 1);
    }

    public Tank(int tileX, int tileY, float moveDurationSeconds, int health, int bulletDamage) {
        if (bulletDamage <= 0) {
            throw new IllegalArgumentException("Bullet damage must be positive");
        }
        this.bulletDamage = bulletDamage;
        if (health < 0) {
            throw new IllegalArgumentException("Initial health must not be negative");
        }
        this.health = health;
        if (!Float.isFinite(moveDurationSeconds) || moveDurationSeconds <= 0f) {
            throw new IllegalArgumentException("Movement duration must be positive and finite");
        }
        tileCoordinates = new GridPoint2(tileX, tileY);
        destinationTileCoordinates = new GridPoint2(tileCoordinates);
        this.moveDurationSeconds = moveDurationSeconds;
    }

    public void tryMove(Direction direction, Predicate<GridPoint2> canMoveTo) {
        if (isDestroyed() || isMoving()) {
            return;
        }
        this.direction = direction;
        GridPoint2 nextTileCoordinates = direction.getAdjacentTileCoordinates(tileCoordinates);
        if (canMoveTo.test(nextTileCoordinates)) {
            destinationTileCoordinates.set(nextTileCoordinates);
            movementProgress = MOVEMENT_START;
        }
    }

    public void shoot(Consumer<Bullet> registerBullet) {
        if (!isDestroyed()) {
            registerBullet.accept(new Bullet(this,
                    direction.getAdjacentTileCoordinates(tileCoordinates), direction, bulletDamage));
        }
    }

    public void takeDamage(int damage) {
        if (damage <= 0) {
            throw new IllegalArgumentException("Damage must be positive");
        }
        health = (int) Math.max(Integer.MIN_VALUE, (long) health - damage);
    }

    public int getHealth() {
        return health;
    }

    public boolean isDestroyed() {
        return health < 0;
    }

    @Override
    public void update(float deltaSeconds) {
        movementProgress = clamp(movementProgress + deltaSeconds / moveDurationSeconds,
                MOVEMENT_START, MOVEMENT_COMPLETE);
        if (!isMoving()) {
            tileCoordinates.set(destinationTileCoordinates);
        }
    }

    public boolean isMoving() {
        return movementProgress < MOVEMENT_COMPLETE;
    }

    @Override
    public boolean occupiesTile(GridPoint2 tileCoordinates) {
        return this.tileCoordinates.equals(tileCoordinates)
                || destinationTileCoordinates.equals(tileCoordinates);
    }

    @Override
    public GridPoint2 getTileCoordinates() {
        return new GridPoint2(tileCoordinates);
    }

    @Override
    public GridPoint2 getDestinationTileCoordinates() {
        return new GridPoint2(destinationTileCoordinates);
    }

    @Override
    public float getMovementProgress() {
        return movementProgress;
    }

    @Override
    public float getRotationDegrees() {
        return direction.getRotationDegrees();
    }
}

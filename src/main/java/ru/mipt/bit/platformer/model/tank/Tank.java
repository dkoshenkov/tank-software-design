package ru.mipt.bit.platformer.model.tank;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.MotionState;
import ru.mipt.bit.platformer.model.Updatable;

import java.util.function.Predicate;

import static com.badlogic.gdx.math.MathUtils.clamp;

public class Tank implements Updatable, MotionState {
    private static final float MOVEMENT_START = 0f;
    private static final float MOVEMENT_COMPLETE = 1f;
    private static final float DEFAULT_MOVE_DURATION_SECONDS = 0.4f;

    private final GridPoint2 tileCoordinates;
    private final GridPoint2 destinationTileCoordinates;
    private final float moveDurationSeconds;
    private float movementProgress = MOVEMENT_COMPLETE;
    private Direction direction = Direction.RIGHT;

    public Tank(int tileX, int tileY) {
        this(tileX, tileY, DEFAULT_MOVE_DURATION_SECONDS);
    }

    public Tank(int tileX, int tileY, float moveDurationSeconds) {
        if (!Float.isFinite(moveDurationSeconds) || moveDurationSeconds <= 0f) {
            throw new IllegalArgumentException("Movement duration must be positive and finite");
        }
        tileCoordinates = new GridPoint2(tileX, tileY);
        destinationTileCoordinates = new GridPoint2(tileCoordinates);
        this.moveDurationSeconds = moveDurationSeconds;
    }

    public void tryMove(Direction direction, Predicate<GridPoint2> canMoveTo) {
        if (isMoving()) {
            return;
        }
        this.direction = direction;
        GridPoint2 nextTileCoordinates = direction.getAdjacentTileCoordinates(tileCoordinates);
        if (canMoveTo.test(nextTileCoordinates)) {
            destinationTileCoordinates.set(nextTileCoordinates);
            movementProgress = MOVEMENT_START;
        }
    }

    @Override
    public void update(float deltaSeconds) {
        movementProgress = clamp(movementProgress + deltaSeconds / moveDurationSeconds,
                MOVEMENT_START, MOVEMENT_COMPLETE);
        if (!isMoving()) {
            tileCoordinates.set(destinationTileCoordinates);
        }
    }

    private boolean isMoving() {
        return movementProgress < MOVEMENT_COMPLETE;
    }

    public GridPoint2 getTileCoordinates() {
        return new GridPoint2(tileCoordinates);
    }

    public GridPoint2 getDestinationTileCoordinates() {
        return new GridPoint2(destinationTileCoordinates);
    }

    public float getMovementProgress() {
        return movementProgress;
    }

    public float getRotationDegrees() {
        return direction.getRotationDegrees();
    }
}

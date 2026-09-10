package ru.mipt.bit.platformer.tank;

import com.badlogic.gdx.math.GridPoint2;

import java.util.function.Predicate;

import static com.badlogic.gdx.math.MathUtils.clamp;

public class Tank {
    private static final float MOVEMENT_START = 0f;
    private static final float MOVEMENT_COMPLETE = 1f;
    private static final float MOVE_DURATION_SECONDS = 0.4f;

    private final GridPoint2 tileCoordinates;
    private final GridPoint2 destinationTileCoordinates;
    private float movementProgress = MOVEMENT_COMPLETE;
    private Direction direction = Direction.RIGHT;

    public Tank(int tileX, int tileY) {
        tileCoordinates = new GridPoint2(tileX, tileY);
        destinationTileCoordinates = new GridPoint2(tileCoordinates);
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

    public void updateMovement(float deltaSeconds) {
        movementProgress = clamp(movementProgress + deltaSeconds / MOVE_DURATION_SECONDS,
                MOVEMENT_START, MOVEMENT_COMPLETE);
        if (!isMoving()) {
            tileCoordinates.set(destinationTileCoordinates);
        }
    }

    public boolean isMoving() {
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

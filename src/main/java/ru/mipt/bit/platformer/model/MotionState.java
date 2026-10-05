package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface MotionState extends TilePosition {
    GridPoint2 getDestinationTileCoordinates();
    float getMovementProgress();
    float getRotationDegrees();
}

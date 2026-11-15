package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Через этот порт графика узнаёт, куда едет танк, какую часть пути он прошёл и куда повёрнут.
 */
public interface MotionState extends TilePosition {
    GridPoint2 getDestinationTileCoordinates();
    float getMovementProgress();
    float getRotationDegrees();
}

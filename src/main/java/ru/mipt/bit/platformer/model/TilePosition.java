package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Порт для чтения координат клетки.
 */
public interface TilePosition {
    GridPoint2 getTileCoordinates();
}

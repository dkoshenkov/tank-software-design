package ru.mipt.bit.platformer.tank;

import com.badlogic.gdx.math.GridPoint2;

public enum Direction {
    UP(0, 1, 90f),
    LEFT(-1, 0, -180f),
    DOWN(0, -1, -90f),
    RIGHT(1, 0, 0f);

    private final int tileOffsetX;
    private final int tileOffsetY;
    private final float rotationDegrees;

    Direction(int tileOffsetX, int tileOffsetY, float rotationDegrees) {
        this.tileOffsetX = tileOffsetX;
        this.tileOffsetY = tileOffsetY;
        this.rotationDegrees = rotationDegrees;
    }

    public GridPoint2 getAdjacentTileCoordinates(GridPoint2 tileCoordinates) {
        return new GridPoint2(tileCoordinates).add(tileOffsetX, tileOffsetY);
    }

    public float getRotationDegrees() {
        return rotationDegrees;
    }
}

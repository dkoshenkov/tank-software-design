package ru.mipt.bit.platformer.obstacle;

import com.badlogic.gdx.math.GridPoint2;

public class Obstacle {
    private final GridPoint2 tileCoordinates;

    public Obstacle(int tileX, int tileY) {
        tileCoordinates = new GridPoint2(tileX, tileY);
    }

    public GridPoint2 getTileCoordinates() {
        return new GridPoint2(tileCoordinates);
    }

    public boolean occupiesTile(GridPoint2 tileCoordinates) {
        return this.tileCoordinates.equals(tileCoordinates);
    }
}

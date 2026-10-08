package ru.mipt.bit.platformer.model.obstacle;

import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.model.TileBlocker;
import ru.mipt.bit.platformer.model.TilePosition;

public class Tree implements TileBlocker, TilePosition {
    private final GridPoint2 tileCoordinates;

    public Tree(int tileX, int tileY) {
        tileCoordinates = new GridPoint2(tileX, tileY);
    }

    @Override
    public GridPoint2 getTileCoordinates() {
        return new GridPoint2(tileCoordinates);
    }

    @Override
    public boolean occupiesTile(GridPoint2 tileCoordinates) {
        return this.tileCoordinates.equals(tileCoordinates);
    }
}

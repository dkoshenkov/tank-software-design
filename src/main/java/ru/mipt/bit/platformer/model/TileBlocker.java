package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface TileBlocker {
    boolean occupiesTile(GridPoint2 tileCoordinates);
}

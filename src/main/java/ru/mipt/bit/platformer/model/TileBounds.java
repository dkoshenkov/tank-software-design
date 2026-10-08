package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface TileBounds {
    boolean containsTile(GridPoint2 tileCoordinates);
}

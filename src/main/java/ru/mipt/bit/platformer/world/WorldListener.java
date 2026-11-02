package ru.mipt.bit.platformer.world;

import ru.mipt.bit.platformer.model.TilePosition;

public interface WorldListener {
    void objectAdded(TilePosition object);
    void objectRemoved(TilePosition object);
}

package ru.mipt.bit.platformer.world;

import ru.mipt.bit.platformer.model.TilePosition;

/**
 * Мир сообщает, какие объекты появились или исчезли.
 */
public interface WorldListener {
    void objectAdded(TilePosition object);
    void objectRemoved(TilePosition object);
}

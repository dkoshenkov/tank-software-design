package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.level.LevelLayout;

public interface LevelGenerator {
    LevelLayout generate();
}

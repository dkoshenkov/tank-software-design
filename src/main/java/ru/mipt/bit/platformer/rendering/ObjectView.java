package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Рисует один игровой объект.
 */
public interface ObjectView {
    void render(Batch batch);
}

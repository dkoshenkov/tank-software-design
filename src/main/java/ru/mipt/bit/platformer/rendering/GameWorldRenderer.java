package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.List;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public final class GameWorldRenderer {
    private final Batch batch;
    private final Runnable renderMap;
    private final List<ObjectView> views;

    public GameWorldRenderer(Batch batch, Runnable renderMap, List<ObjectView> views) {
        this.batch = batch;
        this.renderMap = renderMap;
        this.views = List.copyOf(views);
    }

    public void render() {
        clearScreen();
        renderMap.run();
        renderObjects();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    private void renderObjects() {
        batch.begin();
        try {
            for (ObjectView view : views) {
                view.render(batch);
            }
        } finally {
            batch.end();
        }
    }
}

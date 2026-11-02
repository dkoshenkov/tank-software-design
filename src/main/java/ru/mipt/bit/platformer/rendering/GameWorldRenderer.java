package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;

import ru.mipt.bit.platformer.model.TilePosition;
import ru.mipt.bit.platformer.world.WorldListener;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public final class GameWorldRenderer implements WorldListener {
    private final Batch batch;
    private final Runnable renderMap;
    private final Map<TilePosition, ObjectView> views = new LinkedHashMap<>();
    private final Function<TilePosition, ObjectView> createView;

    public GameWorldRenderer(Batch batch, Runnable renderMap, Function<TilePosition, ObjectView> createView) {
        this.batch = batch;
        this.renderMap = renderMap;
        this.createView = createView;
    }

    @Override
    public void objectAdded(TilePosition object) {
        views.computeIfAbsent(object, createView);
    }

    @Override
    public void objectRemoved(TilePosition object) {
        views.remove(object);
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
            for (ObjectView view : views.values()) {
                view.render(batch);
            }
        } finally {
            batch.end();
        }
    }
}

package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;

public class LevelMapRenderer implements Disposable {
    private final OrthogonalTiledMapRenderer renderer;

    public LevelMapRenderer(Batch batch, TiledMap map) {
        TiledMapTileLayer groundLayer = (TiledMapTileLayer) map.getLayers().get(0);
        float width = groundLayer.getWidth() * groundLayer.getTileWidth();
        float height = groundLayer.getHeight() * groundLayer.getTileHeight();
        renderer = new OrthogonalTiledMapRenderer(map, batch);
        renderer.setView(new Matrix4().setToOrtho2D(0f, 0f, width, height),
                0f, 0f, width, height);
    }

    public void render() {
        renderer.render();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}

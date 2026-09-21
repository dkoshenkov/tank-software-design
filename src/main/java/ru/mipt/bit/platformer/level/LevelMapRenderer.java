package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;

public class LevelMapRenderer implements Disposable {
    private final OrthogonalTiledMapRenderer renderer;

    public LevelMapRenderer(Batch batch, LevelMapAssets assets) {
        TiledMapTileLayer groundLayer = assets.getGroundLayer();
        renderer = new OrthogonalTiledMapRenderer(assets.getTiledMap(), batch);
        renderer.getViewBounds().set(0f, 0f,
                groundLayer.getWidth() * groundLayer.getTileWidth(),
                groundLayer.getHeight() * groundLayer.getTileHeight());
    }

    public void render() {
        renderer.render();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}

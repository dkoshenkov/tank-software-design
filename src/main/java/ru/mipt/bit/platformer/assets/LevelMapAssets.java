package ru.mipt.bit.platformer.assets;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

public class LevelMapAssets implements Disposable {
    private final TiledMap tiledMap;
    private final TiledMapTileLayer groundLayer;

    public LevelMapAssets(String pathToMap) {
        tiledMap = new TmxMapLoader().load(pathToMap);
        try {
            groundLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        } catch (RuntimeException | Error failure) {
            disposeAfterLoadFailure(failure);
            throw failure;
        }
    }

    private void disposeAfterLoadFailure(Throwable failure) {
        try {
            tiledMap.dispose();
        } catch (RuntimeException | Error disposeFailure) {
            failure.addSuppressed(disposeFailure);
        }
    }

    public TiledMapTileLayer getGroundLayer() {
        return groundLayer;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
    }
}

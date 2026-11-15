package ru.mipt.bit.platformer.assets;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

public class LevelMapAssets implements Disposable {
    private final TiledMap tiledMap;
    private final TiledMapTileLayer groundLayer;

    public LevelMapAssets(String pathToMap, int width, int height) {
        tiledMap = new TmxMapLoader().load(pathToMap);
        try {
            groundLayer = replaceGroundLayer(width, height);
        } catch (RuntimeException | Error failure) {
            disposeAfterLoadFailure(failure);
            throw failure;
        }
    }

    private TiledMapTileLayer replaceGroundLayer(int width, int height) {
        TiledMapTileLayer sourceLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        TiledMapTileLayer layer = repeatGroundLayer(sourceLayer, width, height);
        tiledMap.getLayers().remove(0);
        tiledMap.getLayers().add(layer);
        return layer;
    }

    private TiledMapTileLayer repeatGroundLayer(TiledMapTileLayer source, int width, int height) {
        TiledMapTileLayer layer = new TiledMapTileLayer(width, height,
                (int) source.getTileWidth(), (int) source.getTileHeight());
        for (int y = 0; y < height; y++) {
            repeatGroundRow(source, layer, y, width);
        }
        return layer;
    }

    private void repeatGroundRow(TiledMapTileLayer source, TiledMapTileLayer layer, int y, int width) {
        for (int x = 0; x < width; x++) {
            layer.setCell(x, y, source.getCell(x % source.getWidth(), y % source.getHeight()));
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

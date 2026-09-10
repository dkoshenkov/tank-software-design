package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

public class LevelMap implements Disposable {
    private final TiledMap tiledMap;
    private final TiledMapTileLayer groundLayer;

    public LevelMap(String pathToMap) {
        tiledMap = new TmxMapLoader().load(pathToMap);
        groundLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
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

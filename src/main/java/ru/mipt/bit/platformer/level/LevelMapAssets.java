package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

public class LevelMapAssets implements Disposable {
    private final TiledMap tiledMap;
    private final TiledMapTileLayer groundLayer;
    private final LevelMap levelMap;

    public LevelMapAssets(String pathToMap) {
        tiledMap = new TmxMapLoader().load(pathToMap);
        groundLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        levelMap = new LevelMap(groundLayer.getWidth(), groundLayer.getHeight());
    }

    public LevelMap getLevelMap() {
        return levelMap;
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

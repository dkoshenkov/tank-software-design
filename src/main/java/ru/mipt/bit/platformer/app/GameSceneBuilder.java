package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.assets.LevelMapAssets;
import ru.mipt.bit.platformer.input.keyboard.TankKeyboardController;
import ru.mipt.bit.platformer.model.level.LevelMap;
import ru.mipt.bit.platformer.model.obstacle.Tree;
import ru.mipt.bit.platformer.model.tank.Tank;
import ru.mipt.bit.platformer.rendering.GameWorldRenderer;
import ru.mipt.bit.platformer.rendering.LevelMapRenderer;
import ru.mipt.bit.platformer.rendering.ObjectView;
import ru.mipt.bit.platformer.rendering.ObstacleRenderer;
import ru.mipt.bit.platformer.rendering.TankRenderer;
import ru.mipt.bit.platformer.rendering.TilePositioning;
import ru.mipt.bit.platformer.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

public final class GameSceneBuilder {
    public GameSession build() {
        List<Disposable> ownedResources = new ArrayList<>();
        try {
            LevelMapAssets levelMapAssets = new LevelMapAssets("level.tmx");
            ownedResources.add(levelMapAssets);
            Tank playerTank = new Tank(1, 1);
            Tree tree = new Tree(1, 3);
            GameWorld world = createWorld(levelMapAssets, playerTank, tree);
            GameWorldRenderer worldRenderer = createRenderer(levelMapAssets, playerTank, tree, ownedResources);
            TankKeyboardController keyboardController = createKeyboardController(playerTank, world);
            return new GameSession(world, keyboardController::handleInput, worldRenderer::render, ownedResources);
        } catch (RuntimeException | Error failure) {
            disposeFailedScene(ownedResources, failure);
            throw failure;
        }
    }

    private GameWorldRenderer createRenderer(LevelMapAssets assets, Tank tank, Tree tree,
                                           List<Disposable> ownedResources) {
        TiledMapTileLayer groundLayer = assets.getGroundLayer();
        TilePositioning tilePositioning = new TilePositioning(
                groundLayer.getTileWidth(), groundLayer.getTileHeight());

        Batch batch = new SpriteBatch();
        ownedResources.add(batch);
        List<ObjectView> views = createViews(tank, tree, tilePositioning, ownedResources);
        LevelMapRenderer mapRenderer = new LevelMapRenderer(batch, assets);
        ownedResources.add(mapRenderer);
        return new GameWorldRenderer(batch, mapRenderer::render, views);
    }

    private GameWorld createWorld(LevelMapAssets assets, Tank tank, Tree tree) {
        TiledMapTileLayer groundLayer = assets.getGroundLayer();
        LevelMap levelMap = new LevelMap(groundLayer.getWidth(), groundLayer.getHeight());
        return new GameWorld(List.of(tank), levelMap, List.of(tree));
    }

    private TankKeyboardController createKeyboardController(Tank tank, GameWorld world) {
        return new TankKeyboardController(direction -> tank.tryMove(direction, world::canMoveTo));
    }

    private List<ObjectView> createViews(Tank tank, Tree tree, TilePositioning positioning,
                                        List<Disposable> ownedResources) {
        TextureRegion tankTexture = loadTexture("images/tank_blue.png", ownedResources);
        TextureRegion treeTexture = loadTexture("images/greenTree.png", ownedResources);
        ObjectView tankView = new TankRenderer(tank, tankTexture, positioning);
        ObjectView treeView = new ObstacleRenderer(tree, treeTexture, positioning);
        return List.of(tankView, treeView);
    }

    private TextureRegion loadTexture(String path, List<Disposable> ownedResources) {
        Texture texture = new Texture(path);
        ownedResources.add(texture);
        return new TextureRegion(texture);
    }

    private void disposeFailedScene(List<Disposable> resources, Throwable failure) {
        while (!resources.isEmpty()) {
            try {
                resources.remove(resources.size() - 1).dispose();
            } catch (RuntimeException | Error disposeFailure) {
                failure.addSuppressed(disposeFailure);
            }
        }
    }
}

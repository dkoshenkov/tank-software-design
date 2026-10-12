package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.assets.LevelMapAssets;
import ru.mipt.bit.platformer.input.keyboard.TankKeyboardController;
import ru.mipt.bit.platformer.level.LevelGenerator;
import ru.mipt.bit.platformer.model.level.LevelLayout;
import ru.mipt.bit.platformer.model.TileBlocker;
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
    private final LevelGenerator levelGenerator;

    public GameSceneBuilder(LevelGenerator levelGenerator) {
        this.levelGenerator = levelGenerator;
    }

    public GameSession build() {
        List<Disposable> ownedResources = new ArrayList<>();
        try {
            LevelLayout layout = levelGenerator.generate();
            LevelMapAssets levelMapAssets = new LevelMapAssets("level.tmx", layout.getWidth(), layout.getHeight());
            ownedResources.add(levelMapAssets);
            GridPoint2 playerPosition = layout.getPlayerPosition();
            Tank playerTank = new Tank(playerPosition.x, playerPosition.y);
            List<Tree> trees = createTrees(layout);
            GameWorld world = createWorld(layout, playerTank, trees);
            GameWorldRenderer worldRenderer = createRenderer(levelMapAssets, playerTank, trees, ownedResources);
            TankKeyboardController keyboardController = createKeyboardController(playerTank, world);
            return new GameSession(world, keyboardController::handleInput, worldRenderer::render, ownedResources);
        } catch (RuntimeException | Error failure) {
            disposeFailedScene(ownedResources, failure);
            throw failure;
        }
    }

    private List<Tree> createTrees(LevelLayout layout) {
        List<Tree> trees = new ArrayList<>();
        for (GridPoint2 position : layout.getTreePositions()) {
            trees.add(new Tree(position.x, position.y));
        }
        return trees;
    }

    private GameWorldRenderer createRenderer(LevelMapAssets assets, Tank tank, List<Tree> trees,
                                           List<Disposable> ownedResources) {
        TiledMapTileLayer groundLayer = assets.getGroundLayer();
        TilePositioning tilePositioning = new TilePositioning(
                groundLayer.getTileWidth(), groundLayer.getTileHeight());

        Batch batch = new SpriteBatch();
        ownedResources.add(batch);
        List<ObjectView> views = createViews(tank, trees, tilePositioning, ownedResources);
        LevelMapRenderer mapRenderer = new LevelMapRenderer(batch, assets);
        ownedResources.add(mapRenderer);
        return new GameWorldRenderer(batch, mapRenderer::render, views);
    }

    private GameWorld createWorld(LevelLayout layout, Tank tank, List<Tree> trees) {
        LevelMap levelMap = new LevelMap(layout.getWidth(), layout.getHeight());
        List<TileBlocker> obstacles = new ArrayList<>(trees);
        return new GameWorld(List.of(tank), levelMap, obstacles);
    }

    private TankKeyboardController createKeyboardController(Tank tank, GameWorld world) {
        return new TankKeyboardController(direction -> tank.tryMove(direction, world::canMoveTo));
    }

    private List<ObjectView> createViews(Tank tank, List<Tree> trees, TilePositioning positioning,
                                        List<Disposable> ownedResources) {
        TextureRegion tankTexture = loadTexture("images/tank_blue.png", ownedResources);
        TextureRegion treeTexture = loadTexture("images/greenTree.png", ownedResources);
        List<ObjectView> views = new ArrayList<>();
        views.add(new TankRenderer(tank, tankTexture, positioning));
        for (Tree tree : trees) {
            views.add(new ObstacleRenderer(tree, treeTexture, positioning));
        }
        return views;
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

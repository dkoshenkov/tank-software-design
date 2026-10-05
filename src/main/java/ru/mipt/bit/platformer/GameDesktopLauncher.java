package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.grid.TilePositioning;
import ru.mipt.bit.platformer.level.LevelMap;
import ru.mipt.bit.platformer.level.LevelMapAssets;
import ru.mipt.bit.platformer.level.LevelMapRenderer;
import ru.mipt.bit.platformer.obstacle.ObstacleRenderer;
import ru.mipt.bit.platformer.obstacle.Tree;
import ru.mipt.bit.platformer.rendering.ObjectView;
import ru.mipt.bit.platformer.tank.Tank;
import ru.mipt.bit.platformer.tank.TankKeyboardController;
import ru.mipt.bit.platformer.tank.TankRenderer;
import ru.mipt.bit.platformer.world.GameWorld;
import ru.mipt.bit.platformer.world.GameWorldRenderer;

import java.util.ArrayList;
import java.util.List;

public class GameDesktopLauncher extends ApplicationAdapter {
    private final List<Disposable> ownedResources = new ArrayList<>();
    private GameWorld world;
    private GameWorldRenderer worldRenderer;
    private TankKeyboardController playerKeyboardController;

    @Override
    public void create() {
        LevelMapAssets levelMapAssets = new LevelMapAssets("level.tmx");
        ownedResources.add(levelMapAssets);
        TiledMapTileLayer groundLayer = levelMapAssets.getGroundLayer();
        Tank playerTank = new Tank(1, 1);
        Tree tree = new Tree(1, 3);
        LevelMap levelMap = new LevelMap(groundLayer.getWidth(), groundLayer.getHeight());
        world = new GameWorld(List.of(playerTank), levelMap, List.of(tree));
        worldRenderer = createRenderer(levelMapAssets, playerTank, tree);
        playerKeyboardController = new TankKeyboardController(direction ->
                playerTank.tryMove(direction, world::canMoveTo));
    }

    private GameWorldRenderer createRenderer(LevelMapAssets assets, Tank tank, Tree tree) {
        TiledMapTileLayer groundLayer = assets.getGroundLayer();
        TilePositioning tilePositioning = new TilePositioning(
                groundLayer.getTileWidth(), groundLayer.getTileHeight());

        Batch batch = new SpriteBatch();
        ownedResources.add(batch);
        Texture tankTexture = new Texture("images/tank_blue.png");
        ownedResources.add(tankTexture);
        Texture treeTexture = new Texture("images/greenTree.png");
        ownedResources.add(treeTexture);
        LevelMapRenderer mapRenderer = new LevelMapRenderer(batch, assets);
        ownedResources.add(mapRenderer);

        ObjectView tankView = new TankRenderer(tank, new TextureRegion(tankTexture), tilePositioning);
        ObjectView treeView = new ObstacleRenderer(tree, new TextureRegion(treeTexture), tilePositioning);
        return new GameWorldRenderer(batch, mapRenderer::render, List.of(tankView, treeView));
    }

    @Override
    public void render() {
        playerKeyboardController.handleInput(Gdx.input);
        world.update(Gdx.graphics.getDeltaTime());
        worldRenderer.render();
    }

    @Override
    public void dispose() {
        while (!ownedResources.isEmpty()) {
            int lastIndex = ownedResources.size() - 1;
            Disposable resource = ownedResources.remove(lastIndex);
            resource.dispose();
        }
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration windowConfig = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        windowConfig.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), windowConfig);
    }
}

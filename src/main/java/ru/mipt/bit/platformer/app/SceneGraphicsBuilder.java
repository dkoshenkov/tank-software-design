package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Disposable;

import ru.mipt.bit.platformer.assets.LevelMapAssets;
import ru.mipt.bit.platformer.model.level.LevelLayout;
import ru.mipt.bit.platformer.model.obstacle.Tree;
import ru.mipt.bit.platformer.model.tank.Tank;
import ru.mipt.bit.platformer.rendering.GameWorldRenderer;
import ru.mipt.bit.platformer.rendering.LevelMapRenderer;
import ru.mipt.bit.platformer.rendering.ObjectView;
import ru.mipt.bit.platformer.rendering.ObstacleRenderer;
import ru.mipt.bit.platformer.rendering.TankRenderer;
import ru.mipt.bit.platformer.rendering.TilePositioning;

import java.util.ArrayList;
import java.util.List;

public final class SceneGraphicsBuilder {
    public GameWorldRenderer build(LevelLayout layout, List<Tank> tanks, List<Tree> trees,
                                   List<Disposable> ownedResources) {
        LevelMapAssets assets = new LevelMapAssets("level.tmx", layout.getWidth(), layout.getHeight());
        ownedResources.add(assets);
        TilePositioning positioning = createTilePositioning(assets);
        Batch batch = new SpriteBatch();
        ownedResources.add(batch);
        List<ObjectView> views = createViews(tanks, trees, positioning, ownedResources);
        LevelMapRenderer mapRenderer = new LevelMapRenderer(batch, assets);
        ownedResources.add(mapRenderer);
        return new GameWorldRenderer(batch, mapRenderer::render, views);
    }

    private TilePositioning createTilePositioning(LevelMapAssets assets) {
        TiledMapTileLayer groundLayer = assets.getGroundLayer();
        return new TilePositioning(groundLayer.getTileWidth(), groundLayer.getTileHeight());
    }

    private List<ObjectView> createViews(List<Tank> tanks, List<Tree> trees, TilePositioning positioning,
                                        List<Disposable> ownedResources) {
        TextureRegion tankTexture = loadTexture("images/tank_blue.png", ownedResources);
        TextureRegion treeTexture = loadTexture("images/greenTree.png", ownedResources);
        List<ObjectView> views = createTankViews(tanks, tankTexture, positioning);
        views.addAll(createTreeViews(trees, treeTexture, positioning));
        return views;
    }

    private List<ObjectView> createTankViews(List<Tank> tanks, TextureRegion texture,
                                             TilePositioning positioning) {
        List<ObjectView> views = new ArrayList<>();
        for (Tank tank : tanks) {
            views.add(new TankRenderer(tank, texture, positioning));
        }
        return views;
    }

    private List<ObjectView> createTreeViews(List<Tree> trees, TextureRegion texture,
                                             TilePositioning positioning) {
        List<ObjectView> views = new ArrayList<>();
        for (Tree tree : trees) {
            views.add(new ObstacleRenderer(tree, texture, positioning));
        }
        return views;
    }

    private TextureRegion loadTexture(String path, List<Disposable> ownedResources) {
        Texture texture = new Texture(path);
        ownedResources.add(texture);
        return new TextureRegion(texture);
    }
}

package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Disposable;

import ru.mipt.bit.platformer.assets.LevelMapAssets;
import ru.mipt.bit.platformer.model.TilePosition;
import ru.mipt.bit.platformer.model.bullet.Bullet;
import ru.mipt.bit.platformer.model.level.LevelLayout;
import ru.mipt.bit.platformer.model.obstacle.Tree;
import ru.mipt.bit.platformer.model.tank.Tank;
import ru.mipt.bit.platformer.rendering.GameWorldRenderer;
import ru.mipt.bit.platformer.rendering.HealthBarDecorator;
import ru.mipt.bit.platformer.rendering.LevelMapRenderer;
import ru.mipt.bit.platformer.rendering.ObjectView;
import ru.mipt.bit.platformer.rendering.ObstacleRenderer;
import ru.mipt.bit.platformer.rendering.TankRenderer;
import ru.mipt.bit.platformer.rendering.TilePositioning;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class SceneGraphicsBuilder {
    public GameWorldRenderer build(LevelLayout layout,
                                   List<Disposable> ownedResources, BooleanSupplier healthBarsVisible) {
        LevelMapAssets assets = new LevelMapAssets("level.tmx", layout.getWidth(), layout.getHeight());
        ownedResources.add(assets);
        TiledMapTileLayer groundLayer = assets.getGroundLayer();
        Gdx.graphics.setWindowedMode(groundLayer.getWidth() * (int) groundLayer.getTileWidth(),
                groundLayer.getHeight() * (int) groundLayer.getTileHeight());
        TilePositioning positioning = new TilePositioning(groundLayer.getTileWidth(), groundLayer.getTileHeight());
        Batch batch = new SpriteBatch();
        ownedResources.add(batch);
        TextureRegion tankTexture = loadTexture("images/tank_blue.png", ownedResources);
        TextureRegion treeTexture = loadTexture("images/greenTree.png", ownedResources);
        TextureRegion bulletTexture = createTexture(8, pixels -> {
            pixels.setColor(1f, 0.85f, 0.2f, 1f);
            pixels.fillCircle(3, 3, 3);
        }, ownedResources);
        TextureRegion healthBarTexture = createTexture(1, pixels -> {
            pixels.setColor(Color.WHITE);
            pixels.fill();
        }, ownedResources);
        LevelMapRenderer mapRenderer = new LevelMapRenderer(batch, assets.getTiledMap());
        ownedResources.add(mapRenderer);
        return new GameWorldRenderer(batch, mapRenderer::render, object ->
                createView(object, tankTexture, treeTexture, bulletTexture,
                        healthBarTexture, positioning, healthBarsVisible));
    }

    private ObjectView createView(TilePosition object, TextureRegion tankTexture,
                                  TextureRegion treeTexture, TextureRegion bulletTexture,
                                  TextureRegion healthBarTexture, TilePositioning positioning,
                                  BooleanSupplier healthBarsVisible) {
        if (object instanceof Tank) {
            Tank tank = (Tank) object;
            return new HealthBarDecorator(new TankRenderer(tank, tankTexture, positioning),
                    tank, tank::getHealth, tank::getMaxHealth, healthBarTexture, positioning, healthBarsVisible);
        }
        if (object instanceof Tree) {
            return new ObstacleRenderer(object, treeTexture, positioning);
        }
        if (object instanceof Bullet) {
            return new ObstacleRenderer(object, bulletTexture, positioning);
        }
        throw new IllegalArgumentException("No view for " + object.getClass().getName());
    }

    private TextureRegion createTexture(int size, Consumer<Pixmap> paint, List<Disposable> ownedResources) {
        Pixmap pixels = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        try {
            paint.accept(pixels);
            Texture texture = new Texture(pixels);
            ownedResources.add(texture);
            return new TextureRegion(texture);
        } finally {
            pixels.dispose();
        }
    }

    private TextureRegion loadTexture(String path, List<Disposable> ownedResources) {
        Texture texture = new Texture(path);
        ownedResources.add(texture);
        return new TextureRegion(texture);
    }
}

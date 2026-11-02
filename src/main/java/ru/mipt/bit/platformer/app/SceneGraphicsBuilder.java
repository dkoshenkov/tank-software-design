package ru.mipt.bit.platformer.app;

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
import ru.mipt.bit.platformer.rendering.LevelMapRenderer;
import ru.mipt.bit.platformer.rendering.ObjectView;
import ru.mipt.bit.platformer.rendering.ObstacleRenderer;
import ru.mipt.bit.platformer.rendering.TankRenderer;
import ru.mipt.bit.platformer.rendering.TilePositioning;

import java.util.List;

public final class SceneGraphicsBuilder {
    public GameWorldRenderer build(LevelLayout layout,
                                   List<Disposable> ownedResources) {
        LevelMapAssets assets = new LevelMapAssets("level.tmx", layout.getWidth(), layout.getHeight());
        ownedResources.add(assets);
        TilePositioning positioning = createTilePositioning(assets);
        Batch batch = new SpriteBatch();
        ownedResources.add(batch);
        TextureRegion tankTexture = loadTexture("images/tank_blue.png", ownedResources);
        TextureRegion treeTexture = loadTexture("images/greenTree.png", ownedResources);
        TextureRegion bulletTexture = createBulletTexture(ownedResources);
        LevelMapRenderer mapRenderer = new LevelMapRenderer(batch, assets);
        ownedResources.add(mapRenderer);
        return new GameWorldRenderer(batch, mapRenderer::render, object ->
                createView(object, tankTexture, treeTexture, bulletTexture, positioning));
    }

    private TilePositioning createTilePositioning(LevelMapAssets assets) {
        TiledMapTileLayer groundLayer = assets.getGroundLayer();
        return new TilePositioning(groundLayer.getTileWidth(), groundLayer.getTileHeight());
    }

    private ObjectView createView(TilePosition object, TextureRegion tankTexture,
                                  TextureRegion treeTexture, TextureRegion bulletTexture,
                                  TilePositioning positioning) {
        if (object instanceof Tank) {
            return new TankRenderer((Tank) object, tankTexture, positioning);
        }
        if (object instanceof Tree) {
            return new ObstacleRenderer(object, treeTexture, positioning);
        }
        if (object instanceof Bullet) {
            return new ObstacleRenderer(object, bulletTexture, positioning);
        }
        throw new IllegalArgumentException("No view for " + object.getClass().getName());
    }

    private TextureRegion createBulletTexture(List<Disposable> ownedResources) {
        Pixmap pixels = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        try {
            pixels.setColor(1f, 0.85f, 0.2f, 1f);
            pixels.fillCircle(3, 3, 3);
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

package ru.mipt.bit.platformer.obstacle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import ru.mipt.bit.platformer.grid.TilePositioning;

import static ru.mipt.bit.platformer.rendering.TextureRegionRenderer.drawAtNativeSize;

public class ObstacleRenderer implements Disposable {
    private static final float NO_ROTATION_DEGREES = 0f;

    private final Texture texture;
    private final TextureRegion textureRegion;
    private final Rectangle renderBounds;
    private final TilePositioning tilePositioning;

    public ObstacleRenderer(String texturePath, TiledMapTileLayer groundLayer) {
        texture = new Texture(texturePath);
        textureRegion = new TextureRegion(texture);
        renderBounds = new Rectangle(0f, 0f, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        tilePositioning = new TilePositioning(groundLayer);
    }

    public void render(Batch batch, Obstacle obstacle) {
        tilePositioning.centerOnTile(renderBounds, obstacle.getTileCoordinates());
        drawAtNativeSize(batch, textureRegion, renderBounds.x, renderBounds.y, NO_ROTATION_DEGREES);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}

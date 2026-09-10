package ru.mipt.bit.platformer.tank;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.grid.TilePositioning;

import static ru.mipt.bit.platformer.rendering.TextureRegionRenderer.drawAtNativeSize;

public class TankRenderer implements Disposable {
    private final Texture texture;
    private final TextureRegion textureRegion;
    private final Rectangle renderBounds;
    private final TilePositioning tilePositioning;

    public TankRenderer(String texturePath, TiledMapTileLayer groundLayer) {
        texture = new Texture(texturePath);
        textureRegion = new TextureRegion(texture);
        renderBounds = new Rectangle(0f, 0f, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        tilePositioning = new TilePositioning(groundLayer);
    }

    public void render(Batch batch, Tank tank) {
        tilePositioning.positionBetweenTiles(renderBounds, tank.getTileCoordinates(),
                tank.getDestinationTileCoordinates(), tank.getMovementProgress(), Interpolation.smooth);
        drawAtNativeSize(batch, textureRegion, renderBounds.x, renderBounds.y, tank.getRotationDegrees());
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}

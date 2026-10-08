package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import ru.mipt.bit.platformer.model.TilePosition;

public class ObstacleRenderer implements ObjectView {
    private final TilePosition position;
    private final TextureRegion textureRegion;
    private final Rectangle renderBounds;
    private final TilePositioning tilePositioning;

    public ObstacleRenderer(TilePosition position, TextureRegion textureRegion,
                            TilePositioning tilePositioning) {
        this.position = position;
        this.textureRegion = textureRegion;
        this.tilePositioning = tilePositioning;
        renderBounds = new Rectangle(0f, 0f, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }

    @Override
    public void render(Batch batch) {
        tilePositioning.centerOnTile(renderBounds, position.getTileCoordinates());
        batch.draw(textureRegion, renderBounds.x, renderBounds.y);
    }
}

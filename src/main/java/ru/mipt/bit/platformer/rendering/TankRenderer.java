package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.MotionState;

public class TankRenderer implements ObjectView {
    private final MotionState tank;
    private final TextureRegion textureRegion;
    private final Rectangle renderBounds;
    private final TilePositioning tilePositioning;

    public TankRenderer(MotionState tank, TextureRegion textureRegion, TilePositioning tilePositioning) {
        this.tank = tank;
        renderBounds = new Rectangle(0f, 0f, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        this.textureRegion = textureRegion;
        this.tilePositioning = tilePositioning;
    }

    @Override
    public void render(Batch batch) {
        tilePositioning.interpolateCenter(renderBounds, tank.getTileCoordinates(),
                tank.getDestinationTileCoordinates(), tank.getMovementProgress());
        float width = renderBounds.width;
        float height = renderBounds.height;
        batch.draw(
                textureRegion,
                renderBounds.x,
                renderBounds.y,
                width / 2f,
                height / 2f,
                width,
                height,
                1f,
                1f,
                tank.getRotationDegrees());
    }
}

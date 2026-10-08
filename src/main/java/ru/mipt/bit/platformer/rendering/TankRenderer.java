package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import ru.mipt.bit.platformer.model.MotionState;

import static ru.mipt.bit.platformer.rendering.TextureRegionRenderer.drawAtNativeSize;

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
        updateRenderBounds();
        drawAtNativeSize(batch, textureRegion, renderBounds.x, renderBounds.y, tank.getRotationDegrees());
    }

    private void updateRenderBounds() {
        Vector2 center = tileCenter(tank.getTileCoordinates());
        Vector2 destination = tileCenter(tank.getDestinationTileCoordinates());

        center.lerp(destination, tank.getMovementProgress());
        renderBounds.setCenter(center.x, center.y);
    }

    private Vector2 tileCenter(GridPoint2 tile) {
        return new Vector2(
                tilePositioning.tileCenterX(tile),
                tilePositioning.tileCenterY(tile));
    }
}

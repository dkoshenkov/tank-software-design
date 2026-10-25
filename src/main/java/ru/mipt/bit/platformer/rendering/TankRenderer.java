package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.MotionState;

import static com.badlogic.gdx.math.MathUtils.lerp;

public class TankRenderer implements ObjectView {
    private static final float NATIVE_SCALE = 1f;

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
        drawTank(batch);
    }

    private void drawTank(Batch batch) {
        float width = renderBounds.width;
        float height = renderBounds.height;
        batch.draw(
                textureRegion,
                renderBounds.x,
                renderBounds.y,
                rotationOrigin(width),
                rotationOrigin(height),
                width,
                height,
                NATIVE_SCALE,
                NATIVE_SCALE,
                tank.getRotationDegrees());
    }

    private float rotationOrigin(float size) {
        return size / 2f;
    }

    private void updateRenderBounds() {
        GridPoint2 from = tank.getTileCoordinates();
        GridPoint2 to = tank.getDestinationTileCoordinates();
        float progress = tank.getMovementProgress();

        renderBounds.setCenter(
                interpolateCenterX(from, to, progress),
                interpolateCenterY(from, to, progress)
                );
    }

    private float interpolateCenterX(GridPoint2 from, GridPoint2 to, float progress) {
        return lerp(tilePositioning.tileCenterX(from), tilePositioning.tileCenterX(to), progress);
    }

    private float interpolateCenterY(GridPoint2 from, GridPoint2 to, float progress) {
        return lerp(tilePositioning.tileCenterY(from), tilePositioning.tileCenterY(to), progress);
    }
}

package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.MotionState;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public final class HealthBarDecorator implements ObjectView {
    private static final float BAR_WIDTH = 80f;
    private static final float BAR_HEIGHT = 6f;
    private static final float OFFSET_Y = 52f;

    private final ObjectView wrapped;
    private final MotionState motion;
    private final IntSupplier health;
    private final IntSupplier maxHealth;
    private final TextureRegion pixel;
    private final TilePositioning positioning;
    private final BooleanSupplier visible;
    private final Rectangle bounds = new Rectangle(0f, 0f, BAR_WIDTH, BAR_HEIGHT);

    public HealthBarDecorator(ObjectView wrapped, MotionState motion, IntSupplier health,
                              IntSupplier maxHealth, TextureRegion pixel,
                              TilePositioning positioning, BooleanSupplier visible) {
        this.wrapped = wrapped;
        this.motion = motion;
        this.health = health;
        this.maxHealth = maxHealth;
        this.pixel = pixel;
        this.positioning = positioning;
        this.visible = visible;
    }

    @Override
    public void render(Batch batch) {
        wrapped.render(batch);
        if (visible.getAsBoolean()) {
            renderHealthBar(batch);
        }
    }

    private void renderHealthBar(Batch batch) {
        positioning.interpolateCenter(bounds, motion.getTileCoordinates(),
                motion.getDestinationTileCoordinates(), motion.getMovementProgress());
        bounds.y += OFFSET_Y;
        float previousColor = batch.getPackedColor();
        try {
            batch.setColor(Color.DARK_GRAY);
            batch.draw(pixel, bounds.x, bounds.y, BAR_WIDTH, BAR_HEIGHT);
            batch.setColor(Color.GREEN);
            batch.draw(pixel, bounds.x, bounds.y, BAR_WIDTH * healthFraction(), BAR_HEIGHT);
        } finally {
            batch.setPackedColor(previousColor);
        }
    }

    private float healthFraction() {
        int maximum = maxHealth.getAsInt();
        return maximum == 0 ? 0f : Math.max(0, health.getAsInt()) / (float) maximum;
    }
}

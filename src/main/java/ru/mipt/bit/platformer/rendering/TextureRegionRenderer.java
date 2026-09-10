package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class TextureRegionRenderer {
    private TextureRegionRenderer() {
    }

    public static void drawAtNativeSize(Batch batch, TextureRegion region,
                                        float x, float y, float rotationDegrees) {
        int width = region.getRegionWidth();
        int height = region.getRegionHeight();
        batch.draw(region, x, y, width / 2f, height / 2f,
                width, height, 1f, 1f, rotationDegrees);
    }
}

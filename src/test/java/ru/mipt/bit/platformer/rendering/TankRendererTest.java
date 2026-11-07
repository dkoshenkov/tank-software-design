package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.junit.Test;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TankRendererTest {
    @Test public void drawsMovingTankAtInterpolatedCenterWithRotationAroundItsCenter() {
        TextureRegion texture = new TextureRegion() {
            @Override public int getRegionWidth() { return 92; }
            @Override public int getRegionHeight() { return 84; }
        };
        List<List<Object>> draws = new ArrayList<>();
        Batch batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(),
                new Class<?>[]{Batch.class}, (proxy, method, args) -> {
                    if (!method.getName().equals("draw")) {
                        throw new AssertionError("Unexpected batch call: " + method.getName());
                    }
                    draws.add(Arrays.asList(args));
                    return null;
                });
        Tank tank = new Tank(1, 1);
        TankRenderer renderer = new TankRenderer(tank, texture, new TilePositioning(128, 128));
        renderer.render(batch);
        tank.tryMove(Direction.UP, tile -> true);
        tank.update(0.2f);
        renderer.render(batch);
        tank.update(0.2f);
        renderer.render(batch);

        assertEquals(List.of(
                List.of(texture, 146f, 150f, 46f, 42f, 92f, 84f, 1f, 1f, 0f),
                List.of(texture, 146f, 214f, 46f, 42f, 92f, 84f, 1f, 1f, 90f),
                List.of(texture, 146f, 278f, 46f, 42f, 92f, 84f, 1f, 1f, 90f)), draws);
    }
}

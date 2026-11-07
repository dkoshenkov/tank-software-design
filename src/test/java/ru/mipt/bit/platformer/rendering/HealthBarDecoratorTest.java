package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.input.command.ToggleHealthBarsCommand;
import ru.mipt.bit.platformer.input.keyboard.KeyboardController;
import ru.mipt.bit.platformer.input.keyboard.TestInput;
import ru.mipt.bit.platformer.model.MotionState;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import static com.badlogic.gdx.Input.Keys.L;
import static org.junit.Assert.*;

public class HealthBarDecoratorTest {
    private final RecordingBatch recording = new RecordingBatch();
    private final TilePositioning positioning = new TilePositioning(128, 64);
    private final TextureRegion pixel = new TextureRegion();

    @Test public void hiddenDecoratorStillDrawsTheWrappedView() {
        view(new Tank(1, 1), () -> false).render(recording.batch);
        assertEquals(List.of("tank"), recording.calls);
        assertTrue(recording.rectangles.isEmpty());
    }

    @Test public void damageShrinksTheBarRelativeToInitialHealthAndStopsAtZero() {
        Tank tank = new Tank(1, 1, 0.4f, 80);
        ObjectView view = view(tank, () -> true);
        view.render(recording.batch);
        assertEquals(List.of("tank", "bar", "bar"), recording.calls);
        float fullWidth = recording.rectangles.get(0)[2];
        assertTrue(fullWidth > 0f);
        assertEquals(fullWidth, recording.rectangles.get(1)[2], 0f);

        tank.takeDamage(20);
        assertFillWidth(view, fullWidth * 0.75f);
        tank.takeDamage(60);
        assertFillWidth(view, 0f);
        tank.takeDamage(1);
        assertFillWidth(view, 0f);
    }

    @Test public void initiallyZeroHealthProducesAnEmptyFiniteBar() {
        assertFillWidth(view(new Tank(1, 1, 0.4f, 0), () -> true), 0f);
    }

    @Test public void barFollowsInterpolatedTankPositionOnNonSquareTiles() {
        for (Direction direction : Direction.values()) {
            Tank tank = new Tank(1, 1);
            ObjectView view = view(tank, () -> true);
            recording.rectangles.clear();
            view.render(recording.batch);
            float[] start = recording.rectangles.get(0);
            assertEquals(192f, start[0] + start[2] / 2f, 0f);
            assertTrue(start[1] > 96f);
            assertTrue(start[3] > 0f);
            tank.tryMove(direction, tile -> true);
            tank.update(0.2f);
            view.render(recording.batch);
            float[] halfway = recording.rectangles.get(2);
            tank.update(0.2f);
            view.render(recording.batch);
            float[] finish = recording.rectangles.get(4);
            float dx = (tank.getTileCoordinates().x - 1) * 128f;
            float dy = (tank.getTileCoordinates().y - 1) * 64f;
            assertEquals(start[0] + dx, finish[0], 0f);
            assertEquals(start[1] + dy, finish[1], 0f);
            assertEquals(start[0] + dx / 2f, halfway[0], 0f);
            assertEquals(start[1] + dy / 2f, halfway[1], 0f);
        }
    }

    @Test public void restoresBatchColorAfterDrawingAndAfterFailure() {
        float originalColor = recording.color;
        ObjectView view = view(new Tank(0, 0), () -> true);
        view.render(recording.batch);
        assertEquals(originalColor, recording.color, 0f);
        assertNotEquals(recording.colors.get(0), recording.colors.get(1));
        recording.failDrawing = true;
        try {
            view.render(recording.batch);
            fail("Expected drawing failure");
        } catch (IllegalStateException expected) {
            assertEquals("Cannot draw bar", expected.getMessage());
        }
        assertEquals(originalColor, recording.color, 0f);
    }

    @Test public void lTogglesAllTankBarsIncludingNewViewsAndDoesNotRepeatWhileHeld() {
        ToggleHealthBarsCommand command = new ToggleHealthBarsCommand();
        KeyboardController keyboard = new KeyboardController();
        keyboard.bindJustPressed(command, L);
        TestInput input = new TestInput();
        ObjectView player = view(new Tank(0, 0), command::isVisible);
        ObjectView enemy = view(new Tank(1, 0), command::isVisible);
        player.render(recording.batch);
        enemy.render(recording.batch);
        assertTrue(recording.rectangles.isEmpty());
        assertFalse(command.isVisible());

        input.justPressed.add(L);
        keyboard.handleInput(input.input);
        assertTrue(command.isVisible());
        player.render(recording.batch);
        enemy.render(recording.batch);
        assertEquals(4, recording.rectangles.size());

        input.justPressed.clear();
        input.held.add(L);
        keyboard.handleInput(input.input);
        assertTrue(command.isVisible());
        view(new Tank(2, 0), command::isVisible).render(recording.batch);
        assertEquals(6, recording.rectangles.size());

        input.justPressed.add(L);
        keyboard.handleInput(input.input);
        assertFalse(command.isVisible());
        player.render(recording.batch);
        enemy.render(recording.batch);
        assertEquals(6, recording.rectangles.size());
        assertEquals(7, recording.calls.stream().filter("tank"::equals).count());
    }

    @Test public void drawsFromAnotherMotionStateAndLiveHealthSuppliers() {
        MotionState motion = new MotionState() {
            public GridPoint2 getTileCoordinates() { return new GridPoint2(1, 1); }
            public GridPoint2 getDestinationTileCoordinates() { return new GridPoint2(2, 1); }
            public float getMovementProgress() { return 0.5f; }
            public float getRotationDegrees() { return 0f; }
        };
        int[] health = {30};
        ObjectView view = new HealthBarDecorator(batch -> recording.calls.add("object"),
                motion, () -> health[0], () -> 60, pixel, positioning, () -> true);
        view.render(recording.batch);
        assertEquals(List.of("object", "bar", "bar"), recording.calls);
        assertEquals(256f, recording.rectangles.get(0)[0] + 40f, 0f);
        assertEquals(40f, recording.rectangles.get(1)[2], 0f);
        health[0] = 15;
        assertFillWidth(view, 20f);
    }

    private ObjectView view(Tank tank, BooleanSupplier visible) {
        return new HealthBarDecorator(batch -> recording.calls.add("tank"),
                tank, tank::getHealth, tank::getMaxHealth, pixel, positioning, visible);
    }

    private void assertFillWidth(ObjectView view, float expected) {
        recording.rectangles.clear();
        view.render(recording.batch);
        assertEquals(2, recording.rectangles.size());
        assertEquals(expected, recording.rectangles.get(1)[2], 0.0001f);
    }

    private static final class RecordingBatch {
        private final List<String> calls = new ArrayList<>();
        private final List<float[]> rectangles = new ArrayList<>();
        private final List<Float> colors = new ArrayList<>();
        private float color = Color.CORAL.toFloatBits();
        private boolean failDrawing;
        private final Batch batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(),
                new Class<?>[]{Batch.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getPackedColor": return color;
                        case "setPackedColor": color = (Float) args[0]; return null;
                        case "setColor": color = ((Color) args[0]).toFloatBits(); return null;
                        case "draw":
                            if (failDrawing) {
                                throw new IllegalStateException("Cannot draw bar");
                            }
                            calls.add("bar");
                            rectangles.add(new float[]{(Float) args[1], (Float) args[2],
                                    (Float) args[3], (Float) args[4]});
                            colors.add(color);
                            return null;
                        default: throw new AssertionError("Unexpected batch call: " + method.getName());
                    }
                });
    }
}

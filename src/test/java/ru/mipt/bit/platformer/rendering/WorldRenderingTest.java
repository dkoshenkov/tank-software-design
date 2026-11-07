package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import org.junit.Test;
import ru.mipt.bit.platformer.model.TilePosition;
import ru.mipt.bit.platformer.model.level.LevelBounds;
import ru.mipt.bit.platformer.model.tank.Tank;
import ru.mipt.bit.platformer.world.GameWorld;
import ru.mipt.bit.platformer.world.CombatSystem;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WorldRenderingTest {
    @Test public void initialObjectsAndRuntimeChangesUseTheSameSubscription() {
        List<TilePosition> drawn = new ArrayList<>();
        List<String> batchCalls = new ArrayList<>();
        Batch batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(),
                new Class<?>[]{Batch.class}, (proxy, method, args) -> {
                    batchCalls.add(method.getName());
                    return null;
                });
        GameWorldRenderer renderer = new GameWorldRenderer(batch, () -> { },
                object -> ignored -> drawn.add(object));
        GameWorld world = new GameWorld(new LevelBounds(5, 2));
        new CombatSystem(world);
        world.addListener(renderer);
        Tank player = new Tank(0, 0);
        Tank target = new Tank(3, 0, 0.4f, 0);
        world.add(player);
        world.add(target);
        GL20 previousGl = Gdx.gl;
        Gdx.gl = (GL20) Proxy.newProxyInstance(GL20.class.getClassLoader(),
                new Class<?>[]{GL20.class}, (proxy, method, args) -> null);
        try {
            renderer.render();
            assertEquals(List.of(player, target), drawn);
            assertEquals(List.of("begin", "end"), batchCalls);
            player.shoot(world::add);
            drawn.clear();
            renderer.render();
            assertEquals(3, drawn.size());
            world.update(1f);
            drawn.clear();
            renderer.render();
            assertEquals(List.of(player), drawn);
        } finally {
            Gdx.gl = previousGl;
        }
    }
}

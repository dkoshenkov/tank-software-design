package ru.mipt.bit.platformer.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;

public class LevelMapRendererTest {
    @Test public void entireMapFillsTheSharedProjectionRegardlessOfLayerDimensions() {
        int[][] maps = {{10, 6, 128, 128}, {10, 8, 128, 128}, {3, 11, 64, 128}, {1, 1, 128, 64}};
        for (int[] size : maps) {
            Matrix4 projection = new Matrix4().setToOrtho2D(0, 0, 1280, 1024);
            Batch batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(),
                    new Class<?>[]{Batch.class}, (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "setProjectionMatrix": projection.set((Matrix4) args[0]); return null;
                            case "getColor": return Color.WHITE;
                            case "begin":
                            case "end": return null;
                            default: throw new AssertionError("Unexpected batch call: " + method.getName());
                        }
                    });
            TiledMap map = new TiledMap();
            map.getLayers().add(new TiledMapTileLayer(size[0], size[1], size[2], size[3]));
            LevelMapRenderer renderer = new LevelMapRenderer(batch, map);
            renderer.render();

            Vector3 bottomLeft = new Vector3(0, 0, 0).prj(projection);
            Vector3 topRight = new Vector3(size[0] * size[2], size[1] * size[3], 0).prj(projection);
            assertEquals(-1f, bottomLeft.x, 0.00001f);
            assertEquals(-1f, bottomLeft.y, 0.00001f);
            assertEquals(1f, topRight.x, 0.00001f);
            assertEquals(1f, topRight.y, 0.00001f);
            renderer.dispose();
            map.dispose();
        }
    }
}

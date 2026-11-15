package ru.mipt.bit.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GameDesktopLauncherTest {
    @Test public void resizeUsesActualFramebufferSizeAtRetinaAndNormalScale() {
        Graphics previousGraphics = Gdx.graphics;
        GL20 previousGl = Gdx.gl;
        int[] framebuffer = {2560, 1536};
        List<List<Object>> viewports = new ArrayList<>();
        Gdx.graphics = (Graphics) Proxy.newProxyInstance(Graphics.class.getClassLoader(),
                new Class<?>[]{Graphics.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getBackBufferWidth": return framebuffer[0];
                        case "getBackBufferHeight": return framebuffer[1];
                        default: throw new AssertionError("Unexpected graphics call: " + method.getName());
                    }
                });
        Gdx.gl = (GL20) Proxy.newProxyInstance(GL20.class.getClassLoader(),
                new Class<?>[]{GL20.class}, (proxy, method, args) -> {
                    if (!method.getName().equals("glViewport")) {
                        throw new AssertionError("Unexpected GL call: " + method.getName());
                    }
                    viewports.add(Arrays.asList(args));
                    return null;
                });
        try {
            GameDesktopLauncher launcher = new GameDesktopLauncher("level.txt");
            launcher.resize(1280, 768);
            framebuffer[0] = 900;
            framebuffer[1] = 540;
            launcher.resize(900, 540);
            assertEquals(List.of(List.of(0, 0, 2560, 1536), List.of(0, 0, 900, 540)), viewports);
        } finally {
            Gdx.graphics = previousGraphics;
            Gdx.gl = previousGl;
        }
    }
}

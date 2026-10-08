package ru.mipt.bit.platformer.input.keyboard;

import com.badlogic.gdx.Input;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

public final class TestInput {
    public final Set<Integer> held = new HashSet<>();
    public final Input input = (Input) Proxy.newProxyInstance(Input.class.getClassLoader(),
            new Class<?>[]{Input.class}, (proxy, method, args) -> {
                switch (method.getName()) {
                    case "isKeyPressed": return held.contains((Integer) args[0]);
                    default: throw new AssertionError("Unexpected input call: " + method.getName());
                }
            });
}

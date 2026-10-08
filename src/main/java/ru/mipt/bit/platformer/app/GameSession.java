package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class GameSession implements Disposable {
    private final GameWorld world;
    private final Consumer<Input> handleInput;
    private final Runnable render;
    private final List<Disposable> ownedResources;

    GameSession(GameWorld world, Consumer<Input> handleInput,
                Runnable render, List<Disposable> ownedResources) {
        this.world = world;
        this.handleInput = handleInput;
        this.render = render;
        this.ownedResources = new ArrayList<>(ownedResources);
    }

    public void renderFrame(Input input, float deltaSeconds) {
        handleInput.accept(input);
        world.update(deltaSeconds);
        render.run();
    }

    @Override
    public void dispose() {
        while (!ownedResources.isEmpty()) {
            Disposable resource = ownedResources.remove(ownedResources.size() - 1);
            resource.dispose();
        }
    }
}

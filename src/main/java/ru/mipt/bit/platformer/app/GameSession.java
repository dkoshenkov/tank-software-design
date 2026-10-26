package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

public final class GameSession implements Disposable {
    private final GameWorld world;
    private final Runnable updateControls;
    private final Runnable renderScene;
    private final List<Disposable> ownedResources;

    GameSession(GameWorld world, Runnable updateControls,
                Runnable renderScene, List<Disposable> ownedResources) {
        this.world = world;
        this.updateControls = updateControls;
        this.renderScene = renderScene;
        this.ownedResources = new ArrayList<>(ownedResources);
    }

    public void renderFrame(float deltaSeconds) {
        updateControls.run();
        world.update(deltaSeconds);
        renderScene.run();
    }

    @Override
    public void dispose() {
        while (!ownedResources.isEmpty()) {
            Disposable resource = ownedResources.remove(ownedResources.size() - 1);
            resource.dispose();
        }
    }
}

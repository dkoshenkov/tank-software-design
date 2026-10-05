package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Updatable;
import ru.mipt.bit.platformer.model.TileBlocker;
import ru.mipt.bit.platformer.model.TileBounds;

import java.util.List;

public class GameWorld {
    private final List<Updatable> updatables;
    private final TileBounds tileBounds;
    private final List<TileBlocker> obstacles;

    public GameWorld(List<Updatable> updatables, TileBounds tileBounds, List<TileBlocker> obstacles) {
        this.updatables = List.copyOf(updatables);
        this.tileBounds = tileBounds;
        this.obstacles = List.copyOf(obstacles);
    }

    public void update(float deltaSeconds) {
        for (Updatable updatable : updatables) {
            updatable.update(deltaSeconds);
        }
    }

    public boolean canMoveTo(GridPoint2 tileCoordinates) {
        if (!tileBounds.containsTile(tileCoordinates)) {
            return false;
        }
        for (TileBlocker blocker : obstacles) {
            if (blocker.occupiesTile(tileCoordinates)) {
                return false;
            }
        }
        return true;
    }
}

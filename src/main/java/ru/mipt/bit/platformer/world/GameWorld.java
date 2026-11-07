package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.model.TileBlocker;
import ru.mipt.bit.platformer.model.TileBounds;
import ru.mipt.bit.platformer.model.TilePosition;
import ru.mipt.bit.platformer.model.Updatable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class GameWorld {
    private final List<Updatable> updatables = new ArrayList<>();
    private final TileBounds tileBounds;
    private final List<TileBlocker> obstacles = new ArrayList<>();
    private final List<TilePosition> objects = new ArrayList<>();
    private final List<WorldListener> listeners = new ArrayList<>();
    private Predicate<TilePosition> accepts = object -> true;
    private Consumer<TilePosition> afterAdd = object -> { };
    private Consumer<Float> advance = this::updateUpdatables;

    public GameWorld(TileBounds tileBounds) {
        this.tileBounds = tileBounds;
    }

    public GameWorld(List<Updatable> updatables, TileBounds tileBounds, List<TileBlocker> obstacles) {
        this(tileBounds);
        for (Updatable object : updatables) {
            if (object instanceof TilePosition) {
                add((TilePosition) object);
            } else {
                this.updatables.add(object);
            }
        }
        for (TileBlocker object : obstacles) {
            if (object instanceof TilePosition) {
                add((TilePosition) object);
            } else {
                this.obstacles.add(object);
            }
        }
    }

    public void addListener(WorldListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            for (TilePosition object : List.copyOf(objects)) {
                listener.objectAdded(object);
            }
        }
    }

    public void removeListener(WorldListener listener) {
        listeners.remove(listener);
    }

    public void add(TilePosition object) {
        if (objects.contains(object) || !accepts.test(object)) {
            return;
        }
        objects.add(object);
        if (object instanceof Updatable) {
            updatables.add((Updatable) object);
        }
        if (object instanceof TileBlocker) {
            obstacles.add((TileBlocker) object);
        }
        for (WorldListener listener : List.copyOf(listeners)) {
            listener.objectAdded(object);
        }
        afterAdd.accept(object);
    }

    public void remove(TilePosition object) {
        if (!objects.remove(object)) {
            return;
        }
        updatables.remove(object);
        obstacles.remove(object);
        for (WorldListener listener : List.copyOf(listeners)) {
            listener.objectRemoved(object);
        }
    }

    public void update(float deltaSeconds) {
        if (!Float.isFinite(deltaSeconds) || deltaSeconds < 0f) {
            throw new IllegalArgumentException("Delta must be finite and nonnegative");
        }
        advance.accept(deltaSeconds);
    }

    void configure(Predicate<TilePosition> accepts, Consumer<TilePosition> afterAdd,
                   Consumer<Float> advance) {
        this.accepts = accepts;
        this.afterAdd = afterAdd;
        this.advance = advance;
    }

    List<TilePosition> objects() { return List.copyOf(objects); }
    List<TileBlocker> blockers() { return List.copyOf(obstacles); }
    boolean contains(TilePosition object) { return objects.contains(object); }
    boolean containsTile(GridPoint2 tile) { return tileBounds.containsTile(tile); }

    void updateUpdatables(float deltaSeconds) {
        for (Updatable updatable : List.copyOf(updatables)) {
            if (updatables.contains(updatable)) {
                updatable.update(deltaSeconds);
            }
        }
    }

    public boolean canMoveTo(GridPoint2 tileCoordinates) {
        return tileBounds.containsTile(tileCoordinates) && !isBlocked(tileCoordinates);
    }

    private boolean isBlocked(GridPoint2 tileCoordinates) {
        for (TileBlocker blocker : obstacles) {
            if (blocker.occupiesTile(tileCoordinates)) {
                return true;
            }
        }
        return false;
    }
}

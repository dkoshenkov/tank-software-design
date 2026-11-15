package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.model.TileBlocker;
import ru.mipt.bit.platformer.model.TileBounds;
import ru.mipt.bit.platformer.model.TilePosition;
import ru.mipt.bit.platformer.model.Updatable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class GameWorld {
    private final List<Updatable> updatables = new ArrayList<>();
    private final TileBounds tileBounds;
    private final List<TileBlocker> obstacles = new ArrayList<>();
    private final List<TilePosition> objects = new ArrayList<>();
    private final List<WorldListener> listeners = new ArrayList<>();
    private final List<TilePosition> pendingRemovals = new ArrayList<>();
    private final Deque<Runnable> events = new ArrayDeque<>();
    private boolean deliveringEvents;
    private Predicate<TilePosition> accepts = object -> true;
    private Consumer<TilePosition> afterAdd = object -> { };
    private Consumer<Float> advance = this::updateUpdatables;
    private double elapsedSeconds;

    public GameWorld(TileBounds tileBounds) {
        this.tileBounds = tileBounds;
    }

    public GameWorld(List<Updatable> updatables, TileBounds tileBounds, List<TileBlocker> obstacles) {
        this(tileBounds);
        addUpdatables(updatables);
        addObstacles(obstacles);
    }

    private void addUpdatables(List<Updatable> objects) {
        for (Updatable object : objects) {
            if (object instanceof TilePosition) {
                add((TilePosition) object);
            } else {
                updatables.add(object);
            }
        }
    }

    private void addObstacles(List<TileBlocker> objects) {
        for (TileBlocker object : objects) {
            if (object instanceof TilePosition) {
                add((TilePosition) object);
            } else {
                obstacles.add(object);
            }
        }
    }

    public void addListener(WorldListener listener) {
        enqueue(() -> subscribe(listener));
    }

    public void removeListener(WorldListener listener) {
        enqueue(() -> listeners.remove(listener));
    }

    public void add(TilePosition object) {
        enqueue(() -> register(object));
    }

    public void remove(TilePosition object) {
        pendingRemovals.add(object);
        enqueue(() -> {
            pendingRemovals.remove(object);
            unregister(object);
        });
    }

    private void subscribe(WorldListener listener) {
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
        replayObjects(listener);
    }

    private void replayObjects(WorldListener listener) {
        List<TilePosition> delivered = new ArrayList<>();
        try {
            for (TilePosition object : List.copyOf(objects)) {
                delivered.add(object);
                listener.objectAdded(object);
            }
        } catch (RuntimeException | Error failure) {
            rollbackSubscription(listener, delivered, failure);
            throw failure;
        }
    }

    private void rollbackSubscription(WorldListener listener, List<TilePosition> delivered, Throwable failure) {
        listeners.remove(listener);
        for (TilePosition object : delivered) {
            compensate(listener, object, failure);
        }
    }

    private void register(TilePosition object) {
        if (objects.contains(object) || !accepts.test(object)) {
            return;
        }
        addRoles(object);
        publishAdd(object);
        if (!pendingRemovals.contains(object)) {
            afterAdd.accept(object);
        }
    }

    private void addRoles(TilePosition object) {
        objects.add(object);
        if (object instanceof Updatable) {
            updatables.add((Updatable) object);
        }
        if (object instanceof TileBlocker) {
            obstacles.add((TileBlocker) object);
        }
    }

    private void publishAdd(TilePosition object) {
        List<WorldListener> delivered = new ArrayList<>();
        try {
            for (WorldListener listener : List.copyOf(listeners)) {
                delivered.add(listener);
                listener.objectAdded(object);
            }
        } catch (RuntimeException | Error failure) {
            rollbackRegistration(object, delivered, failure);
            throw failure;
        }
    }

    private void rollbackRegistration(TilePosition object, List<WorldListener> delivered, Throwable failure) {
        removeRoles(object);
        for (WorldListener listener : delivered) {
            compensate(listener, object, failure);
        }
    }

    private void unregister(TilePosition object) {
        if (!objects.contains(object)) {
            return;
        }
        removeRoles(object);
        publishRemove(object);
    }

    private void publishRemove(TilePosition object) {
        Throwable failure = null;
        for (WorldListener listener : List.copyOf(listeners)) {
            try {
                listener.objectRemoved(object);
            } catch (RuntimeException | Error eventFailure) {
                failure = accumulateFailure(failure, eventFailure);
            }
        }
        rethrow(failure);
    }

    private void removeRoles(TilePosition object) {
        objects.remove(object);
        updatables.remove(object);
        obstacles.remove(object);
    }

    private void compensate(WorldListener listener, TilePosition object, Throwable failure) {
        try {
            listener.objectRemoved(object);
        } catch (RuntimeException | Error cleanupFailure) {
            if (failure != cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
        }
    }

    private void enqueue(Runnable operation) {
        events.addLast(operation);
        if (deliveringEvents) {
            return;
        }
        deliveringEvents = true;
        try {
            deliverEvents();
        } finally {
            deliveringEvents = false;
        }
    }

    private void deliverEvents() {
        Throwable failure = null;
        while (!events.isEmpty()) {
            try {
                events.removeFirst().run();
            } catch (RuntimeException | Error eventFailure) {
                failure = accumulateFailure(failure, eventFailure);
            }
        }
        rethrow(failure);
    }

    private static Throwable accumulateFailure(Throwable failure, Throwable nextFailure) {
        if (failure == null) {
            return nextFailure;
        }
        if (failure != nextFailure) {
            failure.addSuppressed(nextFailure);
        }
        return failure;
    }

    private static void rethrow(Throwable failure) {
        if (failure instanceof RuntimeException) {
            throw (RuntimeException) failure;
        }
        if (failure instanceof Error) {
            throw (Error) failure;
        }
    }

    public void update(float deltaSeconds) {
        if (!Float.isFinite(deltaSeconds) || deltaSeconds < 0f) {
            throw new IllegalArgumentException("Delta must be finite and nonnegative");
        }
        advance.accept(deltaSeconds);
        elapsedSeconds += deltaSeconds;
    }

    public double getElapsedSeconds() {
        return elapsedSeconds;
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

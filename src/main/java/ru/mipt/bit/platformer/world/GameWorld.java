package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.obstacle.Obstacle;
import ru.mipt.bit.platformer.tank.Tank;

public class GameWorld {
    private final Tank tank;
    private final Obstacle obstacle;

    public GameWorld(Tank tank, Obstacle obstacle) {
        this.tank = tank;
        this.obstacle = obstacle;
    }

    public void update(float deltaSeconds) {
        tank.updateMovement(deltaSeconds);
    }

    public boolean canMoveTo(GridPoint2 tileCoordinates) {
        return !obstacle.occupiesTile(tileCoordinates);
    }

    public Tank getTank() {
        return tank;
    }

    public Obstacle getObstacle() {
        return obstacle;
    }
}

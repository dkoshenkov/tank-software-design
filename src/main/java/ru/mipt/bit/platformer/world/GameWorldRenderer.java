package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.level.LevelMapAssets;
import ru.mipt.bit.platformer.level.LevelMapRenderer;
import ru.mipt.bit.platformer.obstacle.ObstacleRenderer;
import ru.mipt.bit.platformer.tank.TankRenderer;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameWorldRenderer implements Disposable {
    private final GameWorld world;
    private final Batch batch;
    private final LevelMapRenderer levelMapRenderer;
    private final TankRenderer tankRenderer;
    private final ObstacleRenderer obstacleRenderer;

    public GameWorldRenderer(GameWorld world, LevelMapAssets levelMapAssets) {
        this.world = world;
        batch = new SpriteBatch();
        levelMapRenderer = new LevelMapRenderer(batch, levelMapAssets);
        tankRenderer = new TankRenderer("images/tank_blue.png", levelMapAssets.getGroundLayer());
        obstacleRenderer = new ObstacleRenderer("images/greenTree.png", levelMapAssets.getGroundLayer());
    }

    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        levelMapRenderer.render();

        batch.begin();
        tankRenderer.render(batch, world.getTank());
        obstacleRenderer.render(batch, world.getObstacle());
        batch.end();
    }

    @Override
    public void dispose() {
        tankRenderer.dispose();
        obstacleRenderer.dispose();
        levelMapRenderer.dispose();
        batch.dispose();
    }
}

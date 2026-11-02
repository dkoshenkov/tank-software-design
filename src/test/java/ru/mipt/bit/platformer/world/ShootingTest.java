package ru.mipt.bit.platformer.world;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.input.TankControls;
import ru.mipt.bit.platformer.input.command.ShootCommand;
import ru.mipt.bit.platformer.input.keyboard.TestInput;
import ru.mipt.bit.platformer.model.TilePosition;
import ru.mipt.bit.platformer.model.bullet.Bullet;
import ru.mipt.bit.platformer.model.level.LevelBounds;
import ru.mipt.bit.platformer.model.obstacle.Tree;
import ru.mipt.bit.platformer.model.tank.Direction;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.badlogic.gdx.Input.Keys.SPACE;
import static org.junit.Assert.*;

public class ShootingTest {
    private final GameWorld world = new GameWorld(new LevelBounds(12, 3));
    private final List<TilePosition> visible = new ArrayList<>();

    public ShootingTest() {
        world.addListener(new WorldListener() {
            public void objectAdded(TilePosition object) { visible.add(object); }
            public void objectRemoved(TilePosition object) { assertTrue(visible.remove(object)); }
        });
    }

    @Test public void adjacentHitUsesConfiguredDamageAndDeathRequiresNegativeHealth() {
        Tank shooter = new Tank(0, 0, 0.4f, 2, 2);
        Tank target = new Tank(1, 0);
        world.add(shooter);
        world.add(target);
        new ShootCommand(shooter, world::add).run();
        assertEquals(0, target.getHealth());
        assertEquals(List.of(shooter, target), visible);
        assertFalse(world.canMoveTo(new GridPoint2(1, 0)));
        shooter.shoot(world::add);
        assertTrue(target.isDestroyed());
        assertEquals(List.of(shooter), visible);
        assertTrue(world.canMoveTo(new GridPoint2(1, 0)));
        target.tryMove(Direction.UP, world::canMoveTo);
        target.shoot(world::add);
        assertFalse(target.isMoving());
        assertEquals(List.of(shooter), visible);
    }

    @Test public void largeDeltaChecksEveryCellAndStopsAtFirstObstacle() {
        Tank shooter = new Tank(0, 0);
        Tree tree = new Tree(3, 0);
        Tank target = new Tank(5, 0);
        world.add(shooter);
        world.add(tree);
        world.add(target);
        shooter.shoot(world::add);
        world.update(10f);
        assertEquals(List.of(shooter, tree, target), visible);
        assertEquals(2, target.getHealth());
        world.remove(tree);
        shooter.shoot(world::add);
        world.update(10f);
        assertEquals(1, target.getHealth());
        assertEquals(List.of(shooter, target), visible);
    }

    @Test public void bulletsDisappearAtMapEdgesIncludingAtSpawn() {
        Tank shooter = new Tank(11, 0);
        world.add(shooter);
        shooter.shoot(world::add);
        assertEquals(List.of(shooter), visible);
        Bullet bullet = new Bullet(shooter, new GridPoint2(10, 1), Direction.RIGHT, 1);
        world.add(bullet);
        world.update(1f);
        assertEquals(List.of(shooter), visible);
    }

    @Test public void bulletsDestroyEachOtherAtSpawnAndDuringFlight() {
        Tank shooter = new Tank(0, 0);
        Bullet first = new Bullet(shooter, new GridPoint2(2, 1), Direction.RIGHT, 1);
        Bullet second = new Bullet(shooter, new GridPoint2(2, 1), Direction.LEFT, 1);
        world.add(first);
        world.add(second);
        assertTrue(visible.isEmpty());
        world.add(new Bullet(shooter, new GridPoint2(0, 1), Direction.RIGHT, 1));
        world.add(new Bullet(shooter, new GridPoint2(5, 1), Direction.LEFT, 1));
        world.update(0.5f);
        assertTrue(visible.isEmpty());
    }

    @Test public void movingOwnerDoesNotAbsorbItsOwnShot() {
        Tank shooter = new Tank(0, 0);
        Tank target = new Tank(2, 0);
        world.add(shooter);
        world.add(target);
        shooter.tryMove(Direction.RIGHT, world::canMoveTo);
        target.tryMove(Direction.LEFT, world::canMoveTo);
        shooter.shoot(world::add);
        assertEquals(3, visible.size());
        world.update(0.1f);
        assertEquals(1, target.getHealth());
        assertEquals(2, shooter.getHealth());
        assertEquals(List.of(shooter, target), visible);
    }

    @Test public void collectsAllCommandsBeforeExecutingAndDeadAiCannotShoot() {
        Tank player = new Tank(0, 0, 0.4f, 2, 3);
        Tank enemy = new Tank(1, 0);
        world.add(player);
        world.add(enemy);
        TestInput input = new TestInput();
        input.justPressed.add(SPACE);
        Random chooseShoot = new Random() {
            @Override public int nextInt(int bound) {
                assertEquals("AI chooses before player's shot executes", 2, enemy.getHealth());
                return bound - 1;
            }
        };
        TankControls controls = new TankControls(input.input, player, List.of(enemy),
                world::canMoveTo, chooseShoot, world::add);
        controls.controlTanks();
        assertTrue(enemy.isDestroyed());
        assertEquals(List.of(player), visible);
        input.justPressed.clear();
        controls.controlTanks();
        assertEquals(List.of(player), visible);
    }

    @Test public void randomAiCanShootAndKeyboardFiresOnlyOnPress() {
        Tank player = new Tank(0, 0);
        Tank enemy = new Tank(5, 1);
        world.add(player);
        world.add(enemy);
        TestInput input = new TestInput();
        Random chooseShoot = new Random() {
            @Override public int nextInt(int bound) { return bound - 1; }
        };
        TankControls controls = new TankControls(input.input, player, List.of(enemy),
                world::canMoveTo, chooseShoot, world::add);
        input.held.add(SPACE);
        controls.controlTanks();
        assertEquals(3, visible.size());
        assertEquals(new GridPoint2(6, 1), visible.get(2).getTileCoordinates());
        input.justPressed.add(SPACE);
        controls.controlTanks();
        // The second AI shot collides with the first; the player's shot survives.
        assertEquals(3, visible.size());
        assertEquals(new GridPoint2(1, 0), visible.get(2).getTileCoordinates());
    }

    @Test public void bulletsFlyingTogetherDoNotHitEachOthersPreviousPositions() {
        Tank shooter = new Tank(0, 0);
        Bullet first = new Bullet(shooter, new GridPoint2(0, 1), Direction.RIGHT, 1);
        Bullet second = new Bullet(shooter, new GridPoint2(1, 1), Direction.RIGHT, 1);
        world.add(first);
        world.add(second);
        world.update(0.3f);
        assertEquals(List.of(first, second), visible);
        assertEquals(new GridPoint2(3, 1), first.getTileCoordinates());
        assertEquals(new GridPoint2(4, 1), second.getTileCoordinates());
    }

    @Test public void hitsTheReservedDestinationOfAMovingTank() {
        Tank shooter = new Tank(0, 0);
        Tank target = new Tank(2, 1);
        world.add(shooter);
        world.add(target);
        target.tryMove(Direction.DOWN, world::canMoveTo);
        shooter.shoot(world::add);
        world.update(0.1f);
        assertEquals(1, target.getHealth());
        assertEquals(List.of(shooter, target), visible);
    }

    @Test public void hitsDepartingTankBeforeItsMovementFinishesWithinOneFrame() {
        assertEquals(1, shootAtDepartingTank(0f, 0.4f));
        assertEquals(1, shootAtDepartingTank(0f, 0.1f, 0.1f, 0.1f, 0.1f));
    }

    @Test public void hitsDepartingTankDuringSixteenMillisecondFrame() {
        assertEquals(1, shootAtDepartingTank(0.295f, 0.09f, 0.016f));
        assertEquals(1, shootAtDepartingTank(0.295f, 0.09f, 0.01f, 0.006f));
    }

    @Test public void missesTankThatLeavesBeforeTheBulletArrives() {
        assertEquals(2, shootAtDepartingTank(0.35f, 0.4f));
        assertEquals(2, shootAtDepartingTank(0.35f, 0.1f, 0.1f, 0.1f, 0.1f));
    }

    private int shootAtDepartingTank(float elapsedBeforeShot, float... frames) {
        GameWorld scene = new GameWorld(new LevelBounds(12, 3));
        Tank shooter = new Tank(0, 0);
        Tank target = new Tank(2, 0);
        scene.add(shooter);
        scene.add(target);
        target.tryMove(Direction.UP, scene::canMoveTo);
        scene.update(elapsedBeforeShot);
        shooter.shoot(scene::add);
        for (float frame : frames) {
            scene.update(frame);
        }
        assertEquals(new GridPoint2(2, 1), target.getTileCoordinates());
        return target.getHealth();
    }

    @Test public void lateSubscriptionReplaysObjectsOnceAndCanUnsubscribe() {
        Tree tree = new Tree(0, 0);
        world.add(tree);
        List<TilePosition> added = new ArrayList<>();
        WorldListener listener = new WorldListener() {
            public void objectAdded(TilePosition object) { added.add(object); }
            public void objectRemoved(TilePosition object) { fail("Unsubscribed listener"); }
        };
        world.addListener(listener);
        world.addListener(listener);
        world.add(tree);
        assertEquals(List.of(tree), added);
        world.removeListener(listener);
        world.remove(tree);
        assertTrue(visible.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNonFiniteDelta() { world.update(Float.NaN); }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeDelta() { world.update(-1f); }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNonPositiveDamage() {
        new Bullet(new Tank(0, 0), new GridPoint2(1, 0), Direction.RIGHT, 0);
    }
}

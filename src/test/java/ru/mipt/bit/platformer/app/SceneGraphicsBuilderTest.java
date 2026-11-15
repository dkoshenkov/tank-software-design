package ru.mipt.bit.platformer.app;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SceneGraphicsBuilderTest {
    @Test public void playerAndEnemySpritesHaveMatchingSizesAndDistinctArmorColors() throws IOException {
        BufferedImage player = ImageIO.read(getClass().getResource("/images/tank_blue.png"));
        BufferedImage enemy = ImageIO.read(getClass().getResource("/images/tank_red.png"));
        assertEquals(player.getWidth(), enemy.getWidth());
        assertEquals(player.getHeight(), enemy.getHeight());

        int x = player.getWidth() / 2;
        int y = player.getHeight() / 2;
        Color playerArmor = new Color(player.getRGB(x, y));
        Color enemyArmor = new Color(enemy.getRGB(x, y));
        assertTrue(playerArmor.getBlue() > playerArmor.getRed());
        assertTrue(enemyArmor.getRed() > enemyArmor.getBlue());
    }
}

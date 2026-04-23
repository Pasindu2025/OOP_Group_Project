/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package User;
import java.awt.*;
import javax.swing.*;
/**
 *
 * @author G
 */


public class RoundedPanel extends JPanel {
    public RoundedPanel() {
        setOpaque(false);
        setBackground(new Color(45, 45, 45)); // Dark Grey Card
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // 30 is the roundness
        g2.dispose();
    }
}

import javax.swing.*;

/**
 * Minecraft Coordinate System Management Tool
 * Main application entry point
 */
public class MinecraftCoordinateTool extends JFrame {
    private CoordinatePanel coordinatePanel;

    public MinecraftCoordinateTool() {
        setTitle("Minecraft Coordinate System Management Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        coordinatePanel = new CoordinatePanel();
        add(coordinatePanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MinecraftCoordinateTool::new);
    }
}

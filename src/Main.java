import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Swing applications should run on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            GameEngine engine = new GameEngine(window);
            window.setEngine(engine);

            // Center the window on the screen
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            // Start the game logic (ask for name, etc)
            engine.start();
        });
    }
}
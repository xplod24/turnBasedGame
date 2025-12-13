import java.awt.Point;

public class DungeonMap {
    private final int width = 5;
    private final int height = 5;
    private boolean[][] explored;
    private Point playerPos;

    public DungeonMap() {
        explored = new boolean[width][height];
        // Start in middle
        playerPos = new Point(2, 2);
        explored[2][2] = true;
    }

    public Point getPlayerPos() { return playerPos; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public boolean isExplored(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;
        return explored[x][y];
    }

    // Moves randomly to an adjacent tile (simulated exploration)
    public void movePlayerRandomly() {
        int dir = (int)(Math.random() * 4);
        int dx = 0, dy = 0;

        switch(dir) {
            case 0: dy = -1; break; // Up
            case 1: dy = 1; break;  // Down
            case 2: dx = -1; break; // Left
            case 3: dx = 1; break;  // Right
        }

        int nx = playerPos.x + dx;
        int ny = playerPos.y + dy;

        // Bounce back if hitting wall
        if (nx < 0 || nx >= width) nx = playerPos.x;
        if (ny < 0 || ny >= height) ny = playerPos.y;

        playerPos.setLocation(nx, ny);
        explored[nx][ny] = true;
    }
}
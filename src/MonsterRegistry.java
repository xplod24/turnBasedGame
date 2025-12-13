import java.util.ArrayList;
import java.util.List;

// Helper to define static monster data for Bestiary and Spawning
public class MonsterRegistry {

    // We create a factory method to generate live instances
    public static Monster spawnGoblin() {
        return new Monster("Goblin", 1, 35, 8, 1,
                new Potion("Minor Potion", "Heals 15", 15, null), 0.4);
    }

    public static Monster spawnOrc() {
        return new Monster("Orc Warrior", 3, 60, 12, 4,
                new Weapon("Orc Axe", "Dmg +8", 8), 0.3);
    }

    public static Monster spawnTroll() {
        return new Monster("Dungeon Troll", 5, 100, 18, 8,
                new Potion("Troll Blood", "Heals 50, -2 STR", 50, new StatusEffect("Sluggish", 3, -2, 2)), 1.0);
    }

    // Used to populate Bestiary Table
    public static List<Monster> getCatalog() {
        List<Monster> catalog = new ArrayList<>();
        catalog.add(spawnGoblin());
        catalog.add(spawnOrc());
        catalog.add(spawnTroll());
        return catalog;
    }
}
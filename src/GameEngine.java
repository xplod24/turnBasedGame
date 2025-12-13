import javax.swing.JOptionPane;
import java.util.List;

public class GameEngine {
    private Player player;
    private Monster currentEnemy;
    private GameWindow window;
    private DungeonMap map;
    private boolean bestiaryUnlocked = false;

    public GameEngine(GameWindow window) {
        this.window = window;
        this.map = new DungeonMap();
    }

    public void start() {
        String name = JOptionPane.showInputDialog(window, "Enter your hero's name:", "New Game", JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.trim().isEmpty()) name = "Hero";

        player = new Player(name);
        player.getInventory().add(new Weapon("Rusty Sword", "Old but sharp", 5));
        player.getInventory().add(new Potion("Health Potion", "Heals 30 HP", 30, null));

        window.initMapGrid(map.getWidth(), map.getHeight());
        window.updateMapDisplay(map);

        refreshUI();

        Logger.log("Welcome, " + name + "!");
        Logger.log("Hover over items in Inventory to see stats.");
        Logger.log("Equip gear to boost your power!");
    }

    public void actionExplore() {
        if (currentEnemy != null && currentEnemy.isAlive()) return;
        map.movePlayerRandomly();
        window.updateMapDisplay(map);
        Logger.log("You move to a new area...");

        int roll = (int) (Math.random() * 100);
        if (roll < 30) {
            Logger.log("Safe room. Restored 10 HP.");
            player.heal(10);
            refreshUI();
        } else if (roll < 50) {
            Logger.log("You found a Treasure Chest!");
            Armor foundArmor = new Armor("Leather Tunic", "Basic protection (+3 Def)", 3);
            player.getInventory().add(foundArmor);
            Logger.log("Obtained: " + foundArmor.getName());
            refreshUI();
        } else {
            Monster enemy;
            if (roll < 75) enemy = MonsterRegistry.spawnGoblin();
            else if (roll < 90) enemy = MonsterRegistry.spawnOrc();
            else enemy = MonsterRegistry.spawnTroll();
            startCombat(enemy);
        }
    }

    private void startCombat(Monster enemy) {
        this.currentEnemy = enemy;
        Logger.log("\n!!! ENEMY ENCOUNTER: " + enemy.getName() + " !!!");
        window.setCombatMode(true);
        refreshUI();
    }

    public void actionAttack() {
        if (currentEnemy == null) return;

        int dmg = player.calculateAttackDamage();
        int actual = Math.max(1, dmg - currentEnemy.getEffectiveDefense());
        Logger.log("You hit " + currentEnemy.getName() + " for " + actual + " dmg.");
        currentEnemy.takeDamage(actual);

        if (!currentEnemy.isAlive()) {
            handleVictory();
        } else {
            enemyTurn();
        }
    }

    private void enemyTurn() {
        if (currentEnemy == null) return;
        int dmg = currentEnemy.calculateAttackDamage();
        int actual = Math.max(0, dmg - player.calculateDefense());
        Logger.log(currentEnemy.getName() + " hits you for " + actual + " dmg.");
        player.takeDamage(actual);

        player.updateEffects();
        currentEnemy.updateEffects();
        refreshUI();

        if (!player.isAlive()) {
            JOptionPane.showMessageDialog(window, "Game Over", "You Died", JOptionPane.ERROR_MESSAGE);
            window.setCombatMode(false);
        }
    }

    public void actionRun() {
        if (Math.random() > 0.5) {
            Logger.log("You ran away!");
            currentEnemy = null;
            window.setCombatMode(false);
            refreshUI();
        } else {
            Logger.log("Cannot escape!");
            enemyTurn();
        }
    }

    private void handleVictory() {
        Logger.log("You defeated " + currentEnemy.getName() + "!");
        player.recordKill(currentEnemy.getName());

        Item loot = currentEnemy.getLoot();
        if (loot != null) {
            Logger.log("It dropped: " + loot.getName());
            player.getInventory().add(loot);
        }

        if (!bestiaryUnlocked) {
            bestiaryUnlocked = true;
            window.unlockBestiary();
            Logger.log(">> BESTIARY UNLOCKED!");
        }

        window.updateBestiary(MonsterRegistry.getCatalog(), player);
        currentEnemy = null;
        window.setCombatMode(false);
        refreshUI();
    }

    public void handleInventoryUse(int index) {
        Item item = player.getInventory().get(index);
        boolean inCombat = (currentEnemy != null);

        // Equip / Unequip Logic
        if (item instanceof Weapon) {
            if (inCombat) {
                Logger.log("Cannot change weapons in combat!");
                return;
            }
            if (player.getEquippedWeapon() == item) {
                player.unequipWeapon();
            } else {
                player.equipWeapon((Weapon) item);
            }
        }
        else if (item instanceof Armor) {
            if (inCombat) {
                Logger.log("Cannot change armor in combat!");
                return;
            }
            if (player.getEquippedArmor() == item) {
                player.unequipArmor();
            } else {
                player.equipArmor((Armor) item);
            }
        }
        else if (item instanceof Potion) {
            ((Potion) item).use(player);
            player.getInventory().remove(index);
            if (inCombat) enemyTurn();
        }

        refreshUI();
    }

    private void refreshUI() {
        window.updatePlayerStats(player);
        window.updateInventoryList(player.getInventory(), player);
        if (currentEnemy != null) window.updateEnemyStats(true, currentEnemy);
        else window.updateEnemyStats(false, null);
    }
}
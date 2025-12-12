import javax.swing.JOptionPane;
import java.util.List;

public class GameEngine {
    private Player player;
    private Monster currentEnemy;
    private GameWindow window;

    public GameEngine(GameWindow window) {
        this.window = window;
    }

    public void start() {
        String name = JOptionPane.showInputDialog(window, "Enter your hero's name:", "New Game", JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.trim().isEmpty()) name = "Hero";

        player = new Player(name);

        // Starter items
        player.getInventory().add(new Weapon("Rusty Sword", "Old but sharp", 5));
        player.getInventory().add(new Potion("Health Potion", "Heals 30 HP", 30, null));

        Logger.log("Welcome, " + name + "! Your journey begins.");
        Logger.log("Check the Inventory tab to equip your sword!");

        updateGuiStats();
        window.updateInventoryList(player.getInventory());
    }

    // --- Actions ---

    public void actionExplore() {
        if (currentEnemy != null && currentEnemy.isAlive()) return;

        Logger.log("----------------------------------");
        Logger.log(player.getName() + " explores...");

        int roll = (int) (Math.random() * 100);

        if (roll < 25) {
            Logger.log("You found a quiet corner. Resting restored 10 HP.");
            player.heal(10);
            updateGuiStats();
        } else if (roll < 45) {
            Logger.log("You found a Treasure Chest!");
            Armor foundArmor = new Armor("Leather Tunic", "Basic protection (+3 Def)", 3);
            player.getInventory().add(foundArmor);
            Logger.log("Obtained: " + foundArmor.getName());
            window.updateInventoryList(player.getInventory());
        } else {
            startCombatEncounter(roll);
        }
    }

    public void actionAttack() {
        if (currentEnemy == null || !currentEnemy.isAlive()) return;

        int dmg = player.calculateAttackDamage();
        int actualDealt = Math.max(1, dmg - currentEnemy.getEffectiveDefense());
        Logger.log("You " + player.getAttackDescription() + " for " + actualDealt + " damage!");
        currentEnemy.takeDamage(actualDealt);

        if (!currentEnemy.isAlive()) {
            handleVictory();
            return;
        }

        performEnemyTurn();
    }

    public void actionRun() {
        if (currentEnemy == null) return;
        if (Math.random() > 0.5) {
            Logger.log("You escaped!");
            currentEnemy = null;
            window.setCombatMode(false);
            updateGuiStats();
        } else {
            Logger.log("Failed to escape!");
            performEnemyTurn();
        }
    }

    public void actionBestiary() {
        player.showBestiary();
    }

    // --- Inventory Handling ---

    public void handleInventoryUse(int index) {
        if (index < 0 || index >= player.getInventory().size()) return;

        Item item = player.getInventory().get(index);
        boolean isCombat = (currentEnemy != null && currentEnemy.isAlive());

        // Rule: Only potions in combat. No gear swapping.
        if (isCombat && !(item instanceof Potion)) {
            Logger.log(">> You cannot change equipment during combat!");
            return;
        }

        if (item instanceof Potion) {
            ((Potion) item).use(player);
            player.getInventory().remove(index);
            window.updateInventoryList(player.getInventory());

            // Using a potion takes a turn in combat
            if (isCombat) {
                performEnemyTurn();
            }
        } else if (item instanceof Weapon) {
            player.equipWeapon((Weapon) item);
            Logger.log("Equipped " + item.getName());
        } else if (item instanceof Armor) {
            player.equipArmor((Armor) item);
            Logger.log("Equipped " + item.getName());
        }

        updateGuiStats();
    }

    // --- Helper Logic ---

    private void startCombatEncounter(int roll) {
        if (roll < 75) {
            currentEnemy = new Monster("Goblin", 35, 8, 1, new Potion("Minor Potion", "Heals 15", 15, null));
        } else if (roll < 90) {
            currentEnemy = new Monster("Orc Warrior", 60, 12, 4, new Weapon("Orc Axe", "Dmg +8", 8));
        } else {
            currentEnemy = new Monster("Dungeon Troll", 100, 18, 8, new Potion("Troll Blood", "Heals 50, -2 STR", 50, new StatusEffect("Sluggish", 3, -2, 2)));
        }

        Logger.log("\n!!! COMBAT STARTED: " + currentEnemy.getName() + " appears! !!!");
        window.setCombatMode(true);
        updateGuiStats();
    }

    private void performEnemyTurn() {
        if (currentEnemy == null || !currentEnemy.isAlive()) return;

        Logger.log(currentEnemy.getName() + " attacks!");

        int enemyDmg = currentEnemy.calculateAttackDamage();
        int dmgToPlayer = Math.max(0, enemyDmg - player.calculateDefense());

        Logger.log(currentEnemy.getName() + " hits you for " + dmgToPlayer + " damage!");
        player.takeDamage(dmgToPlayer);

        player.updateEffects();
        currentEnemy.updateEffects();
        updateGuiStats();

        if (!player.isAlive()) {
            Logger.log("\n*** GAME OVER ***");
            JOptionPane.showMessageDialog(window, "You have fallen.", "Game Over", JOptionPane.ERROR_MESSAGE);
            window.setCombatMode(false);
        }
    }

    private void handleVictory() {
        Logger.log("VICTORY! You defeated " + currentEnemy.getName() + "!");
        player.addToBestiary(currentEnemy.getName());

        Item loot = currentEnemy.getLoot();
        if (loot != null) {
            Logger.log("Loot found: " + loot.getName());
            player.getInventory().add(loot);
            window.updateInventoryList(player.getInventory());
        }

        currentEnemy = null;
        window.setCombatMode(false);
        updateGuiStats();
    }

    private void updateGuiStats() {
        // Collect Gear Info for display
        // Since Player fields are private and we don't have direct getters for equippedItems in the base class,
        // we rely on the player description or similar.
        // Ideally, we'd add getters to Player. For now, we will assume standard.
        String gear = "No Gear";
        // (Note: To show actual gear names, we would need getters in Player for equippedWeapon/Armor.
        // For now, I will display stats which reflect the gear).

        window.updatePlayerStats(
                player.getName(),
                player.getCurrentHp(),
                player.getMaxHp(),
                player.getEffectiveStrength(),
                player.calculateDefense(),
                "" // Placeholder for gear string if getters added
        );

        if (currentEnemy != null && currentEnemy.isAlive()) {
            window.updateEnemyStats(
                    true,
                    currentEnemy.getName(),
                    currentEnemy.getCurrentHp(),
                    currentEnemy.getMaxHp(),
                    currentEnemy.getEffectiveStrength(),
                    currentEnemy.getEffectiveDefense()
            );
        } else {
            window.updateEnemyStats(false, "", 0, 0, 0, 0);
        }
    }
}
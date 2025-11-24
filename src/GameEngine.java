import java.util.Scanner;

public class GameEngine {
    private Scanner scanner;
    private Player player;
    private boolean isRunning;

    public GameEngine() {
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
    }

    public void start() {
        System.out.println("==================================");
        System.out.println("     JAVA CONSOLE RPG LEGENDS     ");
        System.out.println("==================================");
        System.out.print("Enter your hero's name: ");

        String inputName = scanner.nextLine().trim();
        String name = inputName.isEmpty() ? "Hero" : inputName;

        player = new Player(name);

        // Starter items
        player.getInventory().add(new Weapon("Rusty Sword", "Old but sharp", 5));
        player.getInventory().add(new Potion("Health Potion", "Heals 30 HP", 30, null));
        System.out.println("\nWelcome, " + name + "! You have been gifted a Rusty Sword and a Potion.");
        System.out.println("Equip your sword from the inventory to begin fighting.");

        gameLoop();
    }

    private void gameLoop() {
        while (isRunning && player.isAlive()) {
            System.out.println("\n----------------------------------");
            System.out.println("LOCATION: Dark Dungeon Entrance");
            System.out.println("1. Explore (Find Battle)");
            System.out.println("2. Character Status");
            System.out.println("3. Inventory / Equip");
            System.out.println("4. Bestiary");
            System.out.println("5. Quit Game");
            System.out.print("Choose action: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1": explore(); break;
                case "2": player.showStats(); break;
                case "3": manageInventory(); break;
                case "4": player.showBestiary(); break;
                case "5":
                    System.out.println("Exiting game. Goodbye!");
                    isRunning = false;
                    break;
                default: System.out.println("Invalid command.");
            }
        }

        if (!player.isAlive()) {
            System.out.println("\n**********************************");
            System.out.println("           GAME OVER              ");
            System.out.println("   " + player.getName() + " has fallen.");
            System.out.println("**********************************");
        }
    }

    private void explore() {
        System.out.println("\n" + player.getName() + " ventures into the darkness...");
        try { Thread.sleep(800); } catch (InterruptedException e) {} // Small delay for effect

        int roll = (int) (Math.random() * 100);

        if (roll < 25) {
            System.out.println("You found a quiet corner. Resting restored 10 HP.");
            player.heal(10);
        } else if (roll < 45) {
            System.out.println("You found a Treasure Chest!");
            Armor foundArmor = new Armor("Leather Tunic", "Basic protection (+3 Def)", 3);
            player.getInventory().add(foundArmor);
            System.out.println("Obtained: " + foundArmor.getName());
        } else {
            // Battle Encounter
            Monster enemy;
            if (roll < 75) {
                enemy = new Monster("Goblin", 35, 8, 1, new Potion("Minor Potion", "Heals 15", 15, null));
            } else if (roll < 90) {
                enemy = new Monster("Orc Warrior", 60, 12, 4, new Weapon("Orc Axe", "Dmg +8", 8));
            } else {
                enemy = new Monster("Dungeon Troll", 100, 18, 8, new Potion("Troll Blood", "Heals 50, -2 STR", 50, new StatusEffect("Sluggish", 3, -2, 2)));
            }
            startCombat(enemy);
        }
    }

    private void startCombat(Monster enemy) {
        System.out.println("\n!!! COMBAT STARTED: " + enemy.getName() + " approaches! !!!");

        while (player.isAlive() && enemy.isAlive()) {
            // Stats HUD
            System.out.println("\n [ TURN STATUS ] -----------------------");
            System.out.printf(" %-15s HP: %-3d/%-3d | STR: %d | DEF: %d%n",
                    player.getName(), player.getCurrentHp(), player.getMaxHp(), player.getEffectiveStrength(), player.calculateDefense());
            System.out.printf(" %-15s HP: %-3d/%-3d | STR: %d | DEF: %d%n",
                    enemy.getName(), enemy.getCurrentHp(), enemy.getMaxHp(), enemy.getEffectiveStrength(), enemy.getEffectiveDefense());
            System.out.println(" ---------------------------------------");

            // Player Action
            System.out.println("Actions: [A]ttack, [I]nventory, [R]un");
            System.out.print("> ");
            String choice = scanner.nextLine().toUpperCase();

            if (choice.equals("A")) {
                int dmg = player.calculateAttackDamage();
                int actualDealt = Math.max(1, dmg - enemy.getEffectiveDefense());
                System.out.println("You " + player.getAttackDescription() + " for " + actualDealt + " damage!");
                enemy.takeDamage(actualDealt);
            } else if (choice.equals("I")) {
                manageInventory();
                // Inventory does not consume a turn unless an item is used? 
                // For simplicity, we assume managing inventory takes time if you use something.
                // But in this logic, we return to the start of the loop if inventory was just "looked at".
                continue;
            } else if (choice.equals("R")) {
                if (Math.random() > 0.5) {
                    System.out.println("You managed to escape!");
                    return;
                } else {
                    System.out.println("You failed to escape!");
                }
            } else {
                System.out.println("Invalid command. You hesitate and lose your turn!");
            }

            // Check Enemy Death
            if (!enemy.isAlive()) {
                System.out.println("\nVICTORY! You defeated " + enemy.getName() + "!");
                player.addToBestiary(enemy.getName());
                Item loot = enemy.getLoot();
                if (loot != null) {
                    System.out.println("Loot found: " + loot.getName());
                    player.getInventory().add(loot);
                }
                break;
            }

            // Enemy Turn
            System.out.println("\n" + enemy.getName() + " is attacking...");
            try { Thread.sleep(500); } catch (InterruptedException e) {}

            int enemyDmg = enemy.calculateAttackDamage();
            int dmgToPlayer = Math.max(0, enemyDmg - player.calculateDefense());
            System.out.println(enemy.getName() + " hits you for " + dmgToPlayer + " damage!");
            player.takeDamage(dmgToPlayer);

            // End of Turn Updates
            player.updateEffects();
            enemy.updateEffects();
        }
    }

    private void manageInventory() {
        if (player.getInventory().isEmpty()) {
            System.out.println("Your inventory is empty.");
            return;
        }

        System.out.println("\n--- INVENTORY ---");
        for (int i = 0; i < player.getInventory().size(); i++) {
            System.out.println((i + 1) + ". " + player.getInventory().get(i).toString());
        }
        System.out.println("Enter number to use/equip, or 0 to go back:");

        try {
            String input = scanner.nextLine();
            int index = Integer.parseInt(input) - 1;

            if (index == -1) return;

            if (index >= 0 && index < player.getInventory().size()) {
                Item item = player.getInventory().get(index);

                if (item instanceof Potion) {
                    ((Potion) item).use(player);
                    player.getInventory().remove(index);
                } else if (item instanceof Weapon) {
                    player.equipWeapon((Weapon) item);
                } else if (item instanceof Armor) {
                    player.equipArmor((Armor) item);
                } else {
                    System.out.println("You look at the " + item.getName() + ". It does nothing.");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
}
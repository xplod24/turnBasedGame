import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Player extends GameEntity {
    private List<Item> inventory;
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private Set<String> bestiary;

    public Player(String name) {
        super(name, 100, 10, 5); // Start HP: 100, Str: 10, Def: 5
        this.inventory = new ArrayList<>();
        this.bestiary = new HashSet<>();
    }

    public void equipWeapon(Weapon w) {
        this.equippedWeapon = w;
        System.out.println("You equipped: " + w.getName());
    }

    public void equipArmor(Armor a) {
        this.equippedArmor = a;
        System.out.println("You equipped: " + a.getName());
    }

    public int calculateAttackDamage() {
        int weaponDmg = (equippedWeapon != null) ? equippedWeapon.getDamageBonus() : 0;
        int totalStr = getEffectiveStrength() + weaponDmg;
        // Variance: 0.8x to 1.2x
        double variance = 0.8 + (Math.random() * 0.4);
        return (int) (totalStr * variance);
    }

    public int calculateDefense() {
        int armorDef = (equippedArmor != null) ? equippedArmor.getDefenseBonus() : 0;
        return getEffectiveDefense() + armorDef;
    }

    public void addToBestiary(String monsterName) {
        if (!bestiary.contains(monsterName)) {
            bestiary.add(monsterName);
            System.out.println("*** NEW ENTRY ADDED TO BESTIARY: " + monsterName + " ***");
        }
    }

    public void showBestiary() {
        System.out.println("\n=== " + name.toUpperCase() + "'S BESTIARY ===");
        if (bestiary.isEmpty()) System.out.println("No monsters recorded yet.");
        else bestiary.forEach(m -> System.out.println("- " + m));
        System.out.println("==============================\n");
    }

    public void showStats() {
        System.out.println("\n=== " + name + " ===");
        System.out.println("HP: " + currentHp + "/" + maxHp);
        System.out.println("STR: " + getEffectiveStrength() + " (Base: " + baseStrength + ")");
        System.out.println("DEF: " + calculateDefense() + " (Base: " + baseDefense + ")");
        System.out.println("Weapon: " + (equippedWeapon != null ? equippedWeapon.getName() : "Fists"));
        System.out.println("Armor: " + (equippedArmor != null ? equippedArmor.getName() : "Clothes"));
        System.out.println("Active Effects: " + activeEffects.size());
        System.out.println("================\n");
    }

    public List<Item> getInventory() { return inventory; }

    @Override
    public String getAttackDescription() {
        return (equippedWeapon != null) ? "strike with " + equippedWeapon.getName() : "punch with bare hands";
    }
}
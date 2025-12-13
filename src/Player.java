import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Player extends GameEntity {
    private List<Item> inventory;
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private Set<String> killedMonsters;

    public Player(String name) {
        super(name, 1, 100, 10, 5);
        this.inventory = new ArrayList<>();
        this.killedMonsters = new HashSet<>();
    }

    // --- Equipment Logic ---

    public void equipWeapon(Weapon w) {
        this.equippedWeapon = w;
        Logger.log("Equipped weapon: " + w.getName());
    }

    public void unequipWeapon() {
        if (equippedWeapon != null) {
            Logger.log("Unequipped: " + equippedWeapon.getName());
            this.equippedWeapon = null;
        }
    }

    public void equipArmor(Armor a) {
        this.equippedArmor = a;
        Logger.log("Equipped armor: " + a.getName());
    }

    public void unequipArmor() {
        if (equippedArmor != null) {
            Logger.log("Unequipped: " + equippedArmor.getName());
            this.equippedArmor = null;
        }
    }

    public Weapon getEquippedWeapon() { return equippedWeapon; }
    public Armor getEquippedArmor() { return equippedArmor; }

    public boolean isEquipped(Item item) {
        return item == equippedWeapon || item == equippedArmor;
    }

    // --- Combat Logic ---

    public int calculateAttackDamage() {
        int weaponDmg = (equippedWeapon != null) ? equippedWeapon.getDamageBonus() : 0;
        int totalStr = getEffectiveStrength() + weaponDmg;
        double variance = 0.8 + (Math.random() * 0.4);
        return (int) (totalStr * variance);
    }

    public int calculateDefense() {
        int armorDef = (equippedArmor != null) ? equippedArmor.getDefenseBonus() : 0;
        return getEffectiveDefense() + armorDef;
    }

    // --- Utils ---

    public void recordKill(String monsterName) { killedMonsters.add(monsterName); }
    public boolean hasKilled(String monsterName) { return killedMonsters.contains(monsterName); }
    public List<Item> getInventory() { return inventory; }

    @Override
    public String getAttackDescription() {
        return (equippedWeapon != null) ? "strike with " + equippedWeapon.getName() : "punch with bare hands";
    }
}
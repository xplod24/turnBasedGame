public class Monster extends GameEntity implements Lootable {
    private Item loot;

    public Monster(String name, int hp, int str, int def, Item loot) {
        super(name, hp, str, def);
        this.loot = loot;
    }

    public int calculateAttackDamage() {
        // Monsters have less variance (0.9 to 1.1)
        double variance = 0.9 + (Math.random() * 0.2);
        return (int) (getEffectiveStrength() * variance);
    }

    @Override
    public String getAttackDescription() {
        return "attacks ferociously";
    }

    @Override
    public Item getLoot() {
        return loot;
    }
}
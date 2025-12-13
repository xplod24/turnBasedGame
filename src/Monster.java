public class Monster extends GameEntity implements Lootable {
    private Item loot;
    private double dropChance; // 0.0 to 1.0

    public Monster(String name, int level, int hp, int str, int def, Item loot, double dropChance) {
        super(name, level, hp, str, def);
        this.loot = loot;
        this.dropChance = dropChance;
    }

    public int calculateAttackDamage() {
        double variance = 0.9 + (Math.random() * 0.2);
        return (int) (getEffectiveStrength() * variance);
    }

    @Override
    public String getAttackDescription() { return "attacks ferociously"; }

    @Override
    public Item getLoot() {
        if (Math.random() <= dropChance) return loot;
        return null;
    }

    public Item getPossibleLoot() { return loot; }
    public double getDropChance() { return dropChance; }
}
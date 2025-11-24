public class Weapon extends Item {
    private int damageBonus;

    public Weapon(String name, String description, int damageBonus) {
        super(name, description);
        this.damageBonus = damageBonus;
    }

    public int getDamageBonus() { return damageBonus; }
}
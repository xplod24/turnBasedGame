public class Armor extends Item {
    private int defenseBonus;

    public Armor(String name, String description, int defenseBonus) {
        super(name, description);
        this.defenseBonus = defenseBonus;
    }

    public int getDefenseBonus() { return defenseBonus; }
}
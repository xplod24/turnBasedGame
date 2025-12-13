public class Armor extends Item {
    private final int defenseBonus;

    public Armor(String name, String description, int defenseBonus) {
        super(name, description);
        this.defenseBonus = defenseBonus;
    }

    public int getDefenseBonus() { return defenseBonus; }

    @Override
    public String getStatsInfo() {
        return "<html><b>" + name + "</b><br>"
                + "<i>" + description + "</i><br>"
                + "Defense: <font color='blue'>+" + defenseBonus + "</font></html>";
    }
}
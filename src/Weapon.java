public class Weapon extends Item {
    private final int damageBonus;

    public Weapon(String name, String description, int damageBonus) {
        super(name, description);
        this.damageBonus = damageBonus;
    }

    public int getDamageBonus() { return damageBonus; }

    @Override
    public String getStatsInfo() {
        return "<html><b>" + name + "</b><br>"
                + "<i>" + description + "</i><br>"
                + "Attack Power: <font color='red'>+" + damageBonus + "</font></html>";
    }
}
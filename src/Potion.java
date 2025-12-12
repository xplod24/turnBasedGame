public class Potion extends Item implements Consumable {
    private final int healAmount;
    private final StatusEffect effect;

    public Potion(String name, String description, int healAmount, StatusEffect effect) {
        super(name, description);
        this.healAmount = healAmount;
        this.effect = effect;
    }

    @Override
    public void use(Player player) {
        System.out.println("You drank the " + name + ".");
        if (healAmount > 0) player.heal(healAmount);
        if (effect != null) player.addEffect(new StatusEffect(effect));
    }
}
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class GameEntity {
    protected final String name;
    protected final int maxHp;
    protected int currentHp;
    protected int baseStrength;
    protected int baseDefense;
    protected int level; // NEW: Level
    protected List<StatusEffect> activeEffects;

    public GameEntity(String name, int level, int maxHp, int baseStrength, int baseDefense) {
        this.name = name;
        this.level = level;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.baseStrength = baseStrength;
        this.baseDefense = baseDefense;
        this.activeEffects = new ArrayList<>();
    }

    public abstract String getAttackDescription();

    public boolean isAlive() { return currentHp > 0; }

    public void takeDamage(int amount) {
        int actualDamage = Math.max(0, amount);
        this.currentHp -= actualDamage;
        if (this.currentHp < 0) this.currentHp = 0;
    }

    public void heal(int amount) {
        this.currentHp += amount;
        if (this.currentHp > maxHp) this.currentHp = maxHp;
        Logger.log(">> " + this.name + " healed for " + amount + " HP.");
    }

    public void addEffect(StatusEffect effect) {
        activeEffects.add(effect);
        Logger.log(">> " + this.name + " is affected by " + effect.name + "!");
    }

    public void updateEffects() {
        Iterator<StatusEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect effect = iterator.next();
            effect.duration--;
            if (effect.duration <= 0) {
                Logger.log(">> Effect [" + effect.name + "] wore off on " + this.name + ".");
                iterator.remove();
            }
        }
    }

    public int getEffectiveStrength() {
        int modifier = activeEffects.stream().mapToInt(e -> e.strengthMod).sum();
        return Math.max(0, baseStrength + modifier);
    }

    public int getEffectiveDefense() {
        int modifier = activeEffects.stream().mapToInt(e -> e.defenseMod).sum();
        return Math.max(0, baseDefense + modifier);
    }

    public String getName() { return name; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public int getLevel() { return level; }
}
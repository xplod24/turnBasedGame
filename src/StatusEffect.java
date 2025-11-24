public class StatusEffect {
    public String name;
    public int duration; // in turns
    public int strengthMod;
    public int defenseMod;

    public StatusEffect(String name, int duration, int strMod, int defMod) {
        this.name = name;
        this.duration = duration;
        this.strengthMod = strMod;
        this.defenseMod = defMod;
    }

    // Copy constructor to apply fresh instances
    public StatusEffect(StatusEffect other) {
        this.name = other.name;
        this.duration = other.duration;
        this.strengthMod = other.strengthMod;
        this.defenseMod = other.defenseMod;
    }
}

public abstract class Item {
    protected final String name;
    protected final String description;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }

    // Used for Swing Tooltips (HTML format)
    public abstract String getStatsInfo();

    @Override
    public String toString() { return name; }
}
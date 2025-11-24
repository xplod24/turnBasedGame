public abstract class Item {
    protected String name;
    protected String description;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }

    @Override
    public String toString() { return name + " (" + description + ")"; }
}
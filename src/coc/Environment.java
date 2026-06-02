package coc;

public class Environment {

    private StringBuilder prefix;
    private StringBuilder suffix;

    public Environment() {
        prefix = new StringBuilder();
        suffix = new StringBuilder();
    }

    public void bind(String name, String val, String type) {
        prefix.append(String.format("%n(λ(%s : (%s)).%n", name, type));
        suffix.insert(0, String.format("%n)(%s)%n", val));
    }

    public String getPrefix() {
        return prefix.toString();
    }

    public String getSuffix() {
        return suffix.toString();
    }

}

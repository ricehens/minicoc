package coc;

public class Environment {

    private StringBuilder prefix;
    private StringBuilder suffix;

    public Environment() {
        prefix = new StringBuilder();
        suffix = new StringBuilder();
    }

    public void bind(String name, String type, String val) {
        prefix.append(String.format("%nλ(%s : (%s))%n", name, type));
        suffix.insert(0, String.format("%n(%s)%n", val));
    }

    public String getPrefix() {
        return prefix.toString() + String.format("%n(%n");
    }

    public String getSuffix() {
        return String.format("%n)%n") + suffix.toString();
    }

}

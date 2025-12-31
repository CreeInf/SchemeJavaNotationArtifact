package ch.sjna.model;

import java.util.*;

public class EnumDefinition {
    private final List<String> options;

    public EnumDefinition(List<String> options) {
        this.options = new ArrayList<>(options);
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public boolean isValid(String value) {
        return options.contains(value);
    }
}

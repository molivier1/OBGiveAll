package mathano.mathano.enums;


import org.apache.commons.lang3.tuple.Pair;

public enum Placeholders {
    KIT_NAME ("%kitName%"),
    PLAYER_NAME ("%playerName%");

    private final String placeholder;


    private Placeholders (String placeholder) {
        this.placeholder = placeholder;
    }

    public Pair<String, String> set(String string) {
        return Pair.of(placeholder, string);
    }
}

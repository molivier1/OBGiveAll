package mathano.mathano.utils;

import mathano.mathano.managers.ConfigManager;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {
    @SafeVarargs
    public static String getText(String section, String key, Pair<String, String>... values) {
        AtomicReference<String> text = new AtomicReference<>(ChatColor.translateAlternateColorCodes('&' , Objects.requireNonNull(Objects.requireNonNull(ConfigManager.CONFIG.getConfigurationSection(section)).getString(key))));

        Arrays.stream(values).forEach(value->{
            text.set(text.get().replace(value.getKey(), value.getValue()));
        });

        return text.get();
    }
}

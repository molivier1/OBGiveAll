package mathano.mathano.utils;

import mathano.mathano.OBGiveAll;
import mathano.mathano.managers.ConfigManager;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

    public static void asyncSingleTask(Runnable runnable) {
        Executors.newScheduledThreadPool(1).schedule(runnable, 0, TimeUnit.MINUTES);
    }

    public static void asyncRepeatedTask(Runnable runnable, long period, TimeUnit timeUnit) {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 0, period, timeUnit);
    }
}

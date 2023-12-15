package mathano.mathano;

import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Rewards implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console cannot use this command.");
            return true;
        }

        Player player = ((Player) sender).getPlayer();

        FileConfiguration rewardsFile = OBGiveAll.getInstance().getRewardsConfig();

        if(rewardsFile.contains(player.getUniqueId().toString())) {
            rewardsGui(player);
        } else {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas de récompenses en attente");
        }
        return true;
    }

    public void rewardsGui(Player player) {
        Gui rewards = Gui.gui()
                .title(Component.text("Récompenses"))
                .rows(6)
                .disableAllInteractions()
                .create();

        rewards.open(player);
    }
}

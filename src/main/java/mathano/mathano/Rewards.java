package mathano.mathano;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

        FileConfiguration rewardsFile = OBGiveAll.getInstance().getRewardsConfig();
        ConfigurationSection userSection = rewardsFile.getConfigurationSection(player.getUniqueId().toString());
        for (String key : userSection.getKeys(false)) {
            int amount = userSection.getInt(key);
            for (int i =0; i<amount; i++) {
                ItemStack icon = new ItemStack(Material.CHEST);
                ItemMeta meta = icon.getItemMeta();
                meta.setDisplayName(key);
                icon.setItemMeta(meta);
                GuiItem kitItem = ItemBuilder.from(icon).asGuiItem(inventoryClickEvent -> {
                    player.sendMessage(key + " clique");

                    // donne item + decremente/delete du file en CACHE
                    giveKit(player, key);
                });
                rewards.addItem(kitItem);
            }
        }
    }

    public void giveKit(Player player, String kitName) {
        FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();
        ConfigurationSection kitSection = dataKits.getConfigurationSection(kitName);

        int cmp = 0;
        int check = 0;

        while(kitSection.getItemStack(String.valueOf(cmp)) != null) {
            ItemStack currentItem = kitSection.getItemStack(String.valueOf(cmp));

            if(player.getInventory().firstEmpty() != 1) {
                player.getInventory().addItem(currentItem);
            } else {
                //player.item
                player.getWorld().dropItemNaturally(player.getLocation(), currentItem);
                check = 1;
            }
        }

        if(check != 0) {
            player.sendMessage(ChatColor.RED + "Certains items on été drop au sol !");
        }
    }
}

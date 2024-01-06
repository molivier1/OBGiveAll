package mathano.mathano;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
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

        FileConfiguration rewardsFile = OBGiveAll.getInstance().getRewardsConfig();
        ConfigurationSection userSection = rewardsFile.getConfigurationSection(player.getUniqueId().toString());
        FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();
        for (String key : userSection.getKeys(false)) {
            int amount = userSection.getInt(key);
            for (int i =0; i<amount; i++) {
                ItemStack icon = dataKits.getItemStack(key + ".name");
                ItemMeta meta = icon.getItemMeta();
                meta.setDisplayName(key);
                icon.setItemMeta(meta);
                GuiItem kitItem = ItemBuilder.from(icon).asGuiItem(inventoryClickEvent -> {
                    player.sendMessage(key + " clique");

                    // donne item + decremente/delete du file en CACHE
                    giveKit(player, key);

                    int numberOfKits = userSection.getInt(key);
                    numberOfKits--;

                    if(numberOfKits <= 0)
                    {
                        userSection.set(key, null);
                    }
                    else {
                        userSection.set(key, numberOfKits);
                    }

                    if (userSection.getKeys(false).size() <= 0) {
                        rewardsFile.set(player.getUniqueId().toString(), null);
                    }

                    OBGiveAll.getInstance().setRewardsConfig(rewardsFile);

                    rewards.close(player);
                });
                rewards.addItem(kitItem);
            }
        }

        rewards.open(player);
    }

    public void giveKit(Player player, String kitName) {
        FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();
        ConfigurationSection kitSection = dataKits.getConfigurationSection(kitName);

        int numberOfKeys = kitSection.getKeys(false).size() - 1;
        int check = 0;

        for(int i = 0; i < numberOfKeys; i++)
        {
            ItemStack currentItem = kitSection.getItemStack(String.valueOf(i));

            if(player.getInventory().firstEmpty() != -1) {
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

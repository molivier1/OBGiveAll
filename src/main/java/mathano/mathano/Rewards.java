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

    // Gui that displays the available rewards for the player, and they can be redeemed by clicking on the kit's icons.
    // If the player has no available rewards, the Gui won't open and just notify the player that he has no rewards.
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
                    rewards.close(player);

                    player.sendMessage(ChatColor.GREEN + "Vous avez obtenu la récompense " + key);

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
                });
                rewards.addItem(kitItem);
            }
        }

        rewards.open(player);
    }

    // Read through the dataKits config and give every item linked to the specified kit.
    // Decrements by 1 the corresponding kit in the rewards config.
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

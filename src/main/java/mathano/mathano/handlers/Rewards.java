package mathano.mathano.handlers;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import mathano.mathano.enums.Placeholders;
import mathano.mathano.managers.DataKitsManager;
import mathano.mathano.managers.RewardsManager;
import mathano.mathano.utils.ItemGui;
import mathano.mathano.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class Rewards {
    private static final String section = "rewards";

    // Gui that displays the available rewards for the player, and they can be redeemed by clicking on the kit's icons.
    // If the player has no available rewards, the Gui won't open and just notify the player that he has no rewards.
    public static void rewardsGui(Player player) {
        PaginatedGui rewards = Gui.paginated()
                .title(Component.text("Récompenses"))
                .rows(6)
                .pageSize(36)
                .disableAllInteractions()
                .create();


        // Creation of the buttons
        GuiItem leftGuiItem = ItemBuilder.from(ItemGui.leftItem).asGuiItem(inventoryClickEvent -> {
            rewards.previous();
        });
        GuiItem rightGuiItem = ItemBuilder.from(ItemGui.rightItem).asGuiItem(inventoryClickEvent -> {
            rewards.next();
        });
        GuiItem glassPaneItemGui = ItemBuilder.from(ItemGui.glassPaneItem).asGuiItem();

        // Placing of the buttons
        for (int i = 1; i < 10; i++) {
            rewards.setItem(5, i, glassPaneItemGui);
        }
        rewards.setItem(6, 1, glassPaneItemGui);
        rewards.setItem(6, 2, glassPaneItemGui);
        rewards.setItem(6, 3, glassPaneItemGui);
        rewards.setItem(6, 4, leftGuiItem);
        rewards.setItem(6, 5, glassPaneItemGui);
        rewards.setItem(6, 6, rightGuiItem);
        rewards.setItem(6, 7, glassPaneItemGui);
        rewards.setItem(6, 8, glassPaneItemGui);
        rewards.setItem(6, 9, glassPaneItemGui);

        ConfigurationSection userSection = RewardsManager.REWARDS_CONFIG.getConfigurationSection(player.getUniqueId().toString());
        for (String key : userSection.getKeys(false)) {
            int amount = userSection.getInt(key);
            for (int i =0; i<amount; i++) {
                ItemStack icon = DataKitsManager.DATA_KITS_CONFIG.getItemStack(key + ".name");
                ItemMeta meta = icon.getItemMeta();
                meta.setDisplayName(key);
                icon.setItemMeta(meta);
                GuiItem kitItem = ItemBuilder.from(icon).asGuiItem(inventoryClickEvent -> {
                    rewards.close(player);

                    // donne item + decremente/delete du file en CACHE
                    int numberOfKits = userSection.getInt(key);
                    int initialNumber = numberOfKits;
                    numberOfKits--;

                    if(numberOfKits <= 0)
                    {
                        userSection.set(key, null);
                    }
                    else {
                        userSection.set(key, numberOfKits);
                    }

                    if (userSection.getKeys(false).size() <= 0) {
                        RewardsManager.REWARDS_CONFIG.set(player.getUniqueId().toString(), null);
                    }

                    if (initialNumber > 0) {
                        // Message sent when a kit is claimed
                        player.sendMessage(Utils.getText(section, "kitClaimed", Placeholders.KIT_NAME.set(key)));
                        giveKit(player, key);
                    }
                });
                rewards.addItem(kitItem);
            }
        }

        rewards.open(player);
    }

    // Read through the dataKits config and give every item linked to the specified kit.
    // Decrements by 1 the corresponding kit in the rewards config.
    public static void giveKit(Player player, String kitName) {
        ConfigurationSection kitSection = DataKitsManager.DATA_KITS_CONFIG.getConfigurationSection(kitName);

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
            // Message sent whenever item dropped caused by a full inventory
            player.sendMessage(Utils.getText(section, "itemsDropped"));
        }
    }
}

package mathano.mathano.handlers;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import mathano.mathano.OBGiveAll;
import mathano.mathano.database.Logs;
import mathano.mathano.enums.Placeholders;
import mathano.mathano.managers.DataKitsManager;
import mathano.mathano.managers.LogsManager;
import mathano.mathano.managers.RewardsManager;
import mathano.mathano.utils.ItemGui;
import mathano.mathano.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


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


        HashMap<String, Integer> playerRewards = RewardsManager.rewards.get(player.getUniqueId());
        playerRewards.forEach((kitName, amount) -> {
            for (int i = 0; i < amount; i++) {
                GuiItem kitItem = ItemBuilder.from(DataKitsManager.dataKits.get(kitName).getIcon()).asGuiItem(inventoryClickEvent -> {
                    rewards.close(player);

                    if (!RewardsManager.rewards.containsKey(player.getUniqueId())) {
                        return;
                    }

                    int numberOfKits = playerRewards.get(kitName);
                    int initialNumber = numberOfKits;
                    numberOfKits--;

                    if(numberOfKits <= 0)
                    {
                        //userSection.set(key, null);
                        playerRewards.remove(kitName);
                    }
                    else {
                        //userSection.set(key, numberOfKits);
                        playerRewards.put(kitName, numberOfKits);
                    }

                    //userSection.getKeys(false).size() <= 0
                    if (playerRewards.size() <= 0) {
                        //RewardsManager.REWARDS_CONFIG.set(player.getUniqueId().toString(), null);
                        RewardsManager.rewards.remove(player.getUniqueId());
                    } else {
                        RewardsManager.rewards.put(player.getUniqueId(), playerRewards);
                    }

                    if (initialNumber > 0) {
                        // Message sent when a kit is claimed
                        player.sendMessage(Utils.getText(section, "kitClaimed", Placeholders.KIT_NAME.set(kitName)));
                        giveKit(player, kitName);
                        OBGiveAll.INSTANCE.getLogger().info(Utils.getText(section, "playerClaimedKitLog", Placeholders.PLAYER_NAME.set(player.getDisplayName()), Placeholders.KIT_NAME.set(kitName)));
                        Logs log = new Logs();
                        log.setKit_name(kitName);
                        log.setPlayer_uuid(player.getUniqueId());
                        log.setTimestamp(new Timestamp(System.currentTimeMillis()));
                        LogsManager.INSTANCE.insertLogs(log);
                    }
                });
                rewards.addItem(kitItem);
            }
        });

        rewards.open(player);
    }

    // Read through the dataKits config and give every item linked to the specified kit.
    // Decrements by 1 the corresponding kit in the rewards config.
    public static void giveKit(Player player, String kitName) {
        List<ItemStack> items = DataKitsManager.dataKits.get(kitName).getItems();

        AtomicInteger check = new AtomicInteger();

        items.forEach(currentItem -> {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(currentItem);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), currentItem);
                check.set(1);
            }
        });

        if(check.get() != 0) {
            // Message sent whenever item dropped caused by a full inventory
            player.sendMessage(Utils.getText(section, "itemsDropped"));
        }
    }
}

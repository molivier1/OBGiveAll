package mathano.mathano;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import dev.triumphteam.gui.guis.StorageGui;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public class KitsGui implements CommandExecutor {
    // Init of the various buttons as attributes because they are used in different Guis
    // Button to create a kit
    private static final ItemStack createItem = new ItemStack(Material.NETHER_STAR);
    private static final ItemMeta metaCreateItem = createItem.getItemMeta();

    // Button to exit the GUI
    private static final ItemStack exitItem = new ItemStack(Material.PAPER);
    private static final ItemMeta metaExitItem = exitItem.getItemMeta();

    // Delimitation of the GUI
    private static final ItemStack glassPaneItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
    private static final ItemMeta metaGlassPaneItem = glassPaneItem.getItemMeta();


    // Button to save the kit
    private static final ItemStack saveKitItem = new ItemStack(Material.PAPER);
    private static final ItemMeta metaSaveItem = saveKitItem.getItemMeta();

    // Button to go back in mainGUI
    private static final ItemStack goBackItem = new ItemStack(Material.PAPER);
    private static final ItemMeta metaGoBackItem = goBackItem.getItemMeta();

    private static final ItemStack deleteKitItem = new ItemStack(Material.TNT);
    private static final ItemMeta metaDeleteKitItem = deleteKitItem.getItemMeta();

    private static final ItemStack leftItem = new ItemStack(Material.PAPER);
    private static final ItemMeta metaLeftItem = leftItem.getItemMeta();

    private static final ItemStack rightItem = new ItemStack(Material.PAPER);
    private static final ItemMeta metaRightItem = rightItem.getItemMeta();

    private static final ItemStack confirmItem = new ItemStack(Material.PAPER);
    private static final ItemMeta metaConfirmItem = confirmItem.getItemMeta();

    private static int length;

    public KitsGui() {
        // Button to create a kit
        metaCreateItem.setDisplayName(ChatColor.GREEN + "Create a kit");
        createItem.setItemMeta(metaCreateItem);

        // Button to exit the GUI
        metaExitItem.setDisplayName(ChatColor.RED + "Exit");
        metaExitItem.setCustomModelData(10066);
        exitItem.setItemMeta(metaExitItem);

        // Delimitation of the GUI
        metaGlassPaneItem.setDisplayName(" ");
        glassPaneItem.setItemMeta(metaGlassPaneItem);

        // Button to save the kit
        metaSaveItem.setDisplayName(ChatColor.WHITE + "Save kit");
        metaSaveItem.setCustomModelData(10064);
        saveKitItem.setItemMeta(metaSaveItem);

        // Button to go back in mainGUI
        metaGoBackItem.setDisplayName(ChatColor.WHITE + "Back");
        metaGoBackItem.setCustomModelData(10078);
        goBackItem.setItemMeta(metaGoBackItem);

        // Button to delete a kit
        metaDeleteKitItem.setDisplayName(ChatColor.RED + "Delete kit");
        deleteKitItem.setItemMeta(metaDeleteKitItem);

        metaLeftItem.setDisplayName(ChatColor.WHITE + "Page précédente");
        metaLeftItem.setCustomModelData(10051);
        leftItem.setItemMeta(metaLeftItem);

        metaRightItem.setDisplayName(ChatColor.WHITE + "Page suivante");
        metaRightItem.setCustomModelData(10053);
        rightItem.setItemMeta(metaRightItem);

        metaConfirmItem.setDisplayName(ChatColor.GREEN + "Confirmer");
        metaConfirmItem.setCustomModelData(10064);
        confirmItem.setItemMeta(metaConfirmItem);

        length = 0;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console cannot use this command.");
            return true;
        }

        Player player = ((Player) sender).getPlayer();

        mainGui(player);

        return true;
    }

    // Gui that shows every created kits
    public static void mainGui(Player player) {
        // Creation of the main interface
        PaginatedGui mainGui = Gui.paginated()
                .title(Component.text("MainGUI"))
                .rows(6)
                .pageSize(36)
                .disableAllInteractions()
                .create();

        // Creation of the buttons
        GuiItem createGuiItem = ItemBuilder.from(createItem).asGuiItem(inventoryClickEvent -> {
            kitCreationGUI(player);
        });
        GuiItem exitGuiItem = ItemBuilder.from(exitItem).asGuiItem(inventoryClickEvent -> {
            mainGui.close(player);
        });
        GuiItem leftGuiItem = ItemBuilder.from(leftItem).asGuiItem(inventoryClickEvent -> {
            mainGui.previous();
        });
        GuiItem rightGuiItem = ItemBuilder.from(rightItem).asGuiItem(inventoryClickEvent -> {
            mainGui.next();
        });
        GuiItem glassPaneItemGui = ItemBuilder.from(glassPaneItem).asGuiItem();

        // Placing of the buttons
        for (int i = 1; i < 10; i++) {
            mainGui.setItem(5, i, glassPaneItemGui);
        }
        mainGui.setItem(6, 1, createGuiItem);
        mainGui.setItem(6, 2, glassPaneItemGui);
        mainGui.setItem(6, 3, glassPaneItemGui);
        mainGui.setItem(6, 4, leftGuiItem);
        mainGui.setItem(6, 5, glassPaneItemGui);
        mainGui.setItem(6, 6, rightGuiItem);
        mainGui.setItem(6, 7, glassPaneItemGui);
        mainGui.setItem(6, 8, glassPaneItemGui);
        mainGui.setItem(6, 9, exitGuiItem);

        FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();

        ItemStack icon;
        ItemMeta iconMeta;

        for (String key : dataKits.getKeys(false)) {
            //We are getting every key from our config.yml file
            ConfigurationSection section = dataKits.getConfigurationSection(key);
            if (section.getItemStack("name") != null) {
                icon = new ItemStack(section.getItemStack("name"));
                iconMeta = icon.getItemMeta();
                iconMeta.setDisplayName(iconMeta.getDisplayName());
                icon.setItemMeta(iconMeta);
                GuiItem iconGui = ItemBuilder.from(icon).asGuiItem(inventoryClickEvent -> {
                    kitEditGUI(player, inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName());
                });
                mainGui.addItem(iconGui);
            }
        }

        mainGui.open(player);
    }

    // Gui that permits the creation of the kits
    public static void kitCreationGUI(Player player) {
        // Creation of the kit creation interface
        Gui kitCreationGui = Gui.gui()
                .title(Component.text("KitCreationGUI"))
                .rows(6)
                .create();

        // Creation of the buttons
        GuiItem goBackGuiItem = ItemBuilder.from(goBackItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            mainGui(player);
        });
        GuiItem saveKitGuiItem = ItemBuilder.from(saveKitItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            length = 0;
            for (int i = 0; i < 36; i++) {
                if (inventoryClickEvent.getClickedInventory().getItem(i) != null) {
                    length++;
                }
            }

            if (length > 0) {
                new AnvilGUI.Builder()
                        // Either use sync or async variant, not both
                        .onClick((slot, stateSnapshot) -> {
                            if (slot != AnvilGUI.Slot.OUTPUT) {
                                return Collections.emptyList();
                            }

                            if (!stateSnapshot.getText().equalsIgnoreCase("") && !stateSnapshot.getText().equalsIgnoreCase("Nom du kit")
                                    && !OBGiveAll.getInstance().getDataKitsConfig().contains(stateSnapshot.getText())) {
                                saveKit(inventoryClickEvent.getClickedInventory(), player, stateSnapshot.getText(), null);
                                return Arrays.asList(AnvilGUI.ResponseAction.close());
                            } else {
                                if (OBGiveAll.getInstance().getDataKitsConfig().contains(stateSnapshot.getText())) {
                                    player.sendMessage(ChatColor.RED + "Le kit " + stateSnapshot.getText() + " existe déjà !");
                                }
                                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Nom du kit"));
                            }
                        })
                        // Sets the text the GUI should start with
                        .text("Nom du kit")
                        // Set the title of the GUI (only works in 1.14+)
                        .title("Nom du kit")
                        // Set the plugin instance
                        .plugin(OBGiveAll.getInstance())
                        .open(player);
            }
        });
        GuiItem exitGuiItem = ItemBuilder.from(exitItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            kitCreationGui.close(player);
        });
        GuiItem glassPaneItemGui = ItemBuilder.from(glassPaneItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
        });


        // Placing of the buttons
        for (int i = 1; i < 10; i++) {
            kitCreationGui.setItem(5, i, glassPaneItemGui);
        }
        kitCreationGui.setItem(6, 1, goBackGuiItem);
        kitCreationGui.setItem(6, 2, glassPaneItemGui);

        kitCreationGui.setItem(6, 4, glassPaneItemGui);
        kitCreationGui.setItem(6, 5, saveKitGuiItem);
        kitCreationGui.setItem(6, 6, glassPaneItemGui);
        kitCreationGui.setItem(6, 7, glassPaneItemGui);
        kitCreationGui.setItem(6, 8, glassPaneItemGui);
        kitCreationGui.setItem(6, 9, exitGuiItem);

        kitCreationGui.open(player);
    }

    // This Gui shows the Items in the selected kit and permits to edit them
    public static void kitEditGUI(Player player, String name) {
        StorageGui kitEditGui = Gui.storage()
                .title(Component.text("KitEditGUI"))
                .rows(6)
                .create();

        // Creation of the buttons
        GuiItem goBackGuiItem = ItemBuilder.from(goBackItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            mainGui(player);
        });
        GuiItem saveKitGuiItem = ItemBuilder.from(saveKitItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            length = 0;
            for (int i = 0; i < 36; i++) {
                if (inventoryClickEvent.getClickedInventory().getItem(i) != null) {
                    length++;
                }
            }

            if (length > 0) {
                new AnvilGUI.Builder()
                        // Either use sync or async variant, not both
                        .onClick((slot, stateSnapshot) -> {
                            if (slot != AnvilGUI.Slot.OUTPUT) {
                                return Collections.emptyList();
                            }

                            if (!stateSnapshot.getText().equalsIgnoreCase("") && !stateSnapshot.getText().equalsIgnoreCase("Nom du kit")) {
                                if (OBGiveAll.getInstance().getDataKitsConfig().contains(stateSnapshot.getText()) && stateSnapshot.getText().equals(name)) {
                                    saveKit(inventoryClickEvent.getClickedInventory(), player, stateSnapshot.getText(), OBGiveAll.getInstance().getDataKitsConfig().getConfigurationSection(name).getItemStack("name").getItemMeta().getDisplayName());
                                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                                }

                                if (!OBGiveAll.getInstance().getDataKitsConfig().contains(stateSnapshot.getText())) {
                                    saveKit(inventoryClickEvent.getClickedInventory(), player, stateSnapshot.getText(), OBGiveAll.getInstance().getDataKitsConfig().getConfigurationSection(name).getItemStack("name").getItemMeta().getDisplayName());
                                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                                } else {
                                    player.sendMessage(ChatColor.RED + "Le kit " + stateSnapshot.getText() + " existe déjà !");
                                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Nom du kit"));
                                }
                            } else {
                                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Nom du kit"));
                            }
                        })
                        // Sets the text the GUI should start with
                        .text(name)
                        // Set the title of the GUI (only works in 1.14+)
                        .title("Nom du kit")
                        // Set the plugin instance
                        .plugin(OBGiveAll.getInstance())
                        .open(player);
            }
        });
        GuiItem exitGuiItem = ItemBuilder.from(exitItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            kitEditGui.close(player);
        });
        GuiItem glassPaneItemGui = ItemBuilder.from(glassPaneItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
        });
        GuiItem deleteKitItemGui = ItemBuilder.from(deleteKitItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);


            Gui validateDeleteGui = Gui.gui()
                    .title(Component.text("Confirmer la suppression"))
                    .rows(1)
                    .create();

            GuiItem confirmItemGui = ItemBuilder.from(confirmItem).asGuiItem(inventoryClickEvent1 -> {
                deleteKit(inventoryClickEvent.getClickedInventory().getItem(47).getItemMeta().getDisplayName(), player);
                mainGui(player);
            });

            GuiItem cancelItemGui = ItemBuilder.from(exitItem).asGuiItem(inventoryClickEvent1 -> {
                mainGui(player);
            });

            validateDeleteGui.setItem(2, confirmItemGui);
            validateDeleteGui.setItem(6, cancelItemGui);

            validateDeleteGui.open(player);
        });

        // Placing of the buttons
        for (int i = 1; i < 10; i++) {
            kitEditGui.setItem(5, i, glassPaneItemGui);
        }
        kitEditGui.setItem(6, 1, goBackGuiItem);
        kitEditGui.setItem(6, 2, glassPaneItemGui);

        kitEditGui.setItem(6, 4, glassPaneItemGui);
        kitEditGui.setItem(6, 5, saveKitGuiItem);
        kitEditGui.setItem(6, 6, glassPaneItemGui);
        kitEditGui.setItem(6, 7, deleteKitItemGui);
        kitEditGui.setItem(6, 8, glassPaneItemGui);
        kitEditGui.setItem(6, 9, exitGuiItem);


        ConfigurationSection section = OBGiveAll.getInstance().getDataKitsConfig().getConfigurationSection(name);

        int cmp = section.getKeys(false).size() - 1;

        for (int i = 0; i < cmp; i++) {
            String path = Integer.toString(i);

            if (section.isItemStack(path)) {
                kitEditGui.addItem(section.getItemStack(path));
            }
        }

        ItemStack icon = section.getItemStack("name");
        GuiItem iconGui = ItemBuilder.from(icon).asGuiItem();
        kitEditGui.setItem(47, iconGui);

        kitEditGui.open(player);
    }

    // Save the kit in the cache, handles renaming
    public static void saveKit(Inventory kit, Player player, String name, String oldName) {
        // init variables
        ItemStack[] items = new ItemStack[length];
        ItemStack icon;
        ItemMeta iconMeta;
        FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();
        int cmp = 0;

        if (kit.getItem(47) != null) {
            icon = kit.getItem(47);
        } else {
            icon = new ItemStack(Material.CHEST);
        }

        iconMeta = icon.getItemMeta();
        iconMeta.setDisplayName(name);
        icon.setItemMeta(iconMeta);

        if (dataKits.contains(name)) {
            dataKits.set(name, null);
        }

        if (oldName != null && dataKits.contains(oldName)) {
            dataKits.set(oldName, null);

            FileConfiguration rewards = OBGiveAll.getInstance().getRewardsConfig();
            for (String key : rewards.getKeys(false)) {
                ConfigurationSection section = rewards.getConfigurationSection(key);

                if (section.contains(oldName)) {
                    int numberKits = section.getInt(oldName);
                    section.set(name, numberKits);

                    section.set(oldName, null);
                    if (section.getKeys(false).size() <= 0) {
                        rewards.set(player.getUniqueId().toString(), null);
                    }
                }
            }

            OBGiveAll.getInstance().setRewardsConfig(rewards);
        }

        dataKits.set(name + ".name", icon);

        for (int i = 0; i < 36; i++) {
            if (kit.getItem(i) != null) {
                items[cmp] = kit.getItem(i);
                cmp++;
            }
        }

        for (int i = 0; i < cmp; i++) {
            dataKits.set(name + "." + i, items[i]);
        }

        player.sendMessage(ChatColor.GREEN + "Le kit " + name + " a été créé !");

        OBGiveAll.getInstance().setDataKitsConfig(dataKits);
    }

    // Deletes the selected kit from the DataKits and Rewards cache
    public static void deleteKit(String name, Player player) {
        FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();
        dataKits.set(name, null);
        player.sendMessage(ChatColor.GREEN + "Le kit " + name + " a été supprimé !");

        FileConfiguration rewards = OBGiveAll.getInstance().getRewardsConfig();
        for (String key : rewards.getKeys(false)) {
            ConfigurationSection section = rewards.getConfigurationSection(key);

            if (section.contains(name)) {
                section.set(name, null);
                if (section.getKeys(false).size() <= 0) {
                    rewards.set(player.getUniqueId().toString(), null);
                }
            }
        }

        OBGiveAll.getInstance().setRewardsConfig(rewards);
    }
}
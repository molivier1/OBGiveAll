package mathano.mathano;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class KitsGuiCommand implements CommandExecutor, Listener {
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

    private static int length;


    public KitsGuiCommand(OBGiveAll plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

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
        metaSaveItem.setDisplayName("Save kit");
        metaSaveItem.setCustomModelData(10064);
        saveKitItem.setItemMeta(metaSaveItem);

        // Button to go back in mainGUI
        metaGoBackItem.setDisplayName("Back");
        metaGoBackItem.setCustomModelData(10078);
        goBackItem.setItemMeta(metaGoBackItem);

        // Button to delete a kit
        metaDeleteKitItem.setDisplayName(ChatColor.RED + "Delete kit");
        deleteKitItem.setItemMeta(metaDeleteKitItem);

        length = 0;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console cannot use this command.");
            return true;
        }

        Player player = ((Player) sender).getPlayer();

        // Create and open the main GUI for the player
        mainGUI(player);

        return true;
    }

    public static void mainGUI(Player player) {
        // Creation of a gui of size 54 titled MainGUI
        Inventory gui = Bukkit.createInventory(player, 9 * 6, "MainGUI");

        // Set the different items on the gui
        // Set the position of the buttons in the gui
        gui.setItem(45, createItem);
        gui.setItem(53, exitItem);
        // Set the glass pane
        for (int i = 45 - 9; i < 45; i++) {
            gui.setItem(i, glassPaneItem);
        }
        gui.setItem(46, glassPaneItem);
        gui.setItem(47, glassPaneItem);
        gui.setItem(49, glassPaneItem);
        gui.setItem(51, glassPaneItem);
        gui.setItem(52, glassPaneItem);

        FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();
        int cmp = 0;

        ItemStack icon;

        for (String key : dataKits.getKeys(false)) {
            //We are getting every key from our config.yml file
            ConfigurationSection section = dataKits.getConfigurationSection(key);
            if (section.getItemStack("name") != null) {
                icon = new ItemStack(section.getItemStack("name"));
                gui.setItem(cmp, icon);
            }

            cmp++;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                // Metadata given to keep track of the opened inventories
                player.setMetadata("OpenedMainGUI7650", new FixedMetadataValue(OBGiveAll.getInstance(), gui));
            }
        }.runTaskLater(OBGiveAll.getInstance(), 1);

        player.openInventory(gui);
    }

    public static void kitCreationGUI(Player player) {
        // Creation of a gui of size 54 titled MainGUI
        Inventory gui = Bukkit.createInventory(player, 9 * 6, "KitCreationGUI");

        // Set the different items on the gui
        // Set the position of the buttons in the gui
        gui.setItem(45, goBackItem);
        gui.setItem(49, saveKitItem);
        gui.setItem(53, exitItem);

        // Set the glass pane
        for (int i = 45 - 9; i < 45; i++) {
            gui.setItem(i, glassPaneItem);
        }
        gui.setItem(46, glassPaneItem);
        gui.setItem(48, glassPaneItem);
        gui.setItem(50, glassPaneItem);
        gui.setItem(51, glassPaneItem);
        gui.setItem(52, glassPaneItem);

        new BukkitRunnable() {
            @Override
            public void run() {
                // Metadata given to keep track of the opened inventories
                player.setMetadata("OpenedKitCreationGUI7650", new FixedMetadataValue(OBGiveAll.getInstance(), gui));
            }
        }.runTaskLater(OBGiveAll.getInstance(), 1);

        player.openInventory(gui);
    }

    public static void kitEditGUI(Player player, String name) {
        // Creation of a gui of size 54 titled MainGUI
        Inventory gui = Bukkit.createInventory(player, 9 * 6, "kitEditGUI");

        // Set the different items on the gui
        // Set the position of the buttons in the gui
        gui.setItem(45, goBackItem);
        gui.setItem(49, saveKitItem);
        gui.setItem(51, deleteKitItem);
        gui.setItem(53, exitItem);

        // Set the glass pane
        for (int i = 45 - 9; i < 45; i++) {
            gui.setItem(i, glassPaneItem);
        }
        gui.setItem(46, glassPaneItem);
        gui.setItem(48, glassPaneItem);
        gui.setItem(50, glassPaneItem);
        gui.setItem(52, glassPaneItem);

        ConfigurationSection section = OBGiveAll.getInstance().getDataKitsConfig().getConfigurationSection(name);

        ItemStack currentItem;

        int cmp = section.getKeys(false).size();

        for (int i = 1; i < cmp + 1; i++) {
            String path = Integer.toString(i);

            if (section.isItemStack(path)) {
                currentItem = section.getItemStack(path);

                gui.setItem(i - 1, currentItem);
            }
        }

        ItemStack icon = section.getItemStack("name");
        gui.setItem(47, icon);

        new BukkitRunnable() {
            @Override
            public void run() {
                // Metadata given to keep track of the opened inventories
                player.setMetadata("OpenedKitEditGUI7650", new FixedMetadataValue(OBGiveAll.getInstance(), gui));
            }
        }.runTaskLater(OBGiveAll.getInstance(), 1);

        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (player.hasMetadata("OpenedMainGUI7650")) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null && event.getSlot() < 36) {
                kitEditGUI(player, event.getCurrentItem().getItemMeta().getDisplayName());
            }

            // Get the clicked item and perform action
            if (event.getSlot() == 45 && event.getCurrentItem().isSimilar(createItem)) {
                kitCreationGUI(player);
            }

            // if the bedrock block is clicked, close the gui
            if (event.getSlot() == 53 && event.getCurrentItem().isSimilar(exitItem)) {
                player.closeInventory();
            }
        }

        if (player.hasMetadata("OpenedKitCreationGUI7650")) {
            if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(glassPaneItem)) {
                event.setCancelled(true);
            }

            switch (event.getSlot()) {
                case 45:
                    event.setCancelled(true);

                    // if the slot contains the correct item, opens the mainGUI
                    if (event.getCurrentItem().isSimilar(goBackItem)) {
                        mainGUI(player);
                    }
                    break;
                case 49:
                    event.setCancelled(true);

                    length = 0;
                    for (int i = 0; i < 36; i++) {
                        if (event.getClickedInventory().getItem(i) != null) {
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
                                        saveKit(event.getClickedInventory(), player, stateSnapshot.getText());
                                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                                    } else {
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
                    break;
                case 53:
                    event.setCancelled(true);

                    if (event.getCurrentItem().isSimilar(exitItem)) {
                        player.closeInventory();
                    }
                    break;
            }
        }

        if (player.hasMetadata("OpenedKitEditGUI7650")) {
            if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(glassPaneItem)) {
                event.setCancelled(true);
            }

            switch (event.getSlot()) {
                case 45:
                    event.setCancelled(true);

                    // if the slot contains the correct item, opens the mainGUI
                    if (event.getCurrentItem().isSimilar(goBackItem)) {
                        mainGUI(player);
                    }
                    break;
                case 49:
                    event.setCancelled(true);

                    length = 0;
                    for (int i = 0; i < 36; i++) {
                        if (event.getClickedInventory().getItem(i) != null) {
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
                                        saveKit(event.getClickedInventory(), player, stateSnapshot.getText());
                                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                                    } else {
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
                    break;
                case 51:
                    event.setCancelled(true);

                    if (event.getCurrentItem().isSimilar(deleteKitItem)) {
                        System.out.println("On delete le kit");
                    }
                    break;
                case 53:
                    event.setCancelled(true);

                    if (event.getCurrentItem().isSimilar(exitItem)) {
                        player.closeInventory();
                    }
                    break;
            }
        }
    }

    public void saveKit(Inventory kit, Player player, String name) {
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

        try {
            dataKits.save("./plugins/OBGiveAll/dataKits.yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (player.hasMetadata("OpenedMainGUI7650")) {
            player.removeMetadata("OpenedMainGUI7650", OBGiveAll.getInstance());
            System.out.println("Metadata OpenedMainGUI7650 removed from player");
        } else {
            if (player.hasMetadata("OpenedKitCreationGUI7650")) {
                player.removeMetadata("OpenedKitCreationGUI7650", OBGiveAll.getInstance());
                System.out.println("Metadata OpenedKitCreationGUI removed from player");
            }

            if (player.hasMetadata("OpenedKitEditGUI7650")) {
                player.removeMetadata("OpenedKitEditGUI7650", OBGiveAll.getInstance());
                System.out.println("Metadata OpenedKitEditGUI7650 removed from player");
            }
        }
    }
}

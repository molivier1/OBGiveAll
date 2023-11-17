package mathano.mathano;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class KitsGuiCommand implements CommandExecutor, Listener {
    public KitsGuiCommand(OBGiveAll plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
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

        // En faire un public static ou private static final
        // pour éviter de tout le temps refaire la var et mettre les valeurs dans construct

        // Add items to the GUI
        // Creation kit item named "createItem"
        ItemStack createItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta metaCreateItem = createItem.getItemMeta();
        metaCreateItem.setDisplayName(ChatColor.GREEN + "Create a kit");
        createItem.setItemMeta(metaCreateItem);
        // Exit gui item named "exitItem"
        ItemStack exitItem = new ItemStack(Material.PAPER);
        ItemMeta metaExitItem = exitItem.getItemMeta();
        metaExitItem.setDisplayName(ChatColor.RED + "Exit");
        metaExitItem.setCustomModelData(10066);
        exitItem.setItemMeta(metaExitItem);
        // White stained-glass pane
        ItemStack glassPaneItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta metaGlassPaneItem = glassPaneItem.getItemMeta();
        metaGlassPaneItem.setDisplayName(" ");
        glassPaneItem.setItemMeta(metaGlassPaneItem);

        // Set the different items on the gui
        // Set the position of the buttons in the gui
        gui.setItem(45, createItem);
        gui.setItem(53, exitItem);
        // Set the glass pane
        for(int i = 45 - 9; i < 45; i++) {
            gui.setItem(i, glassPaneItem);
        }
        gui.setItem(46, glassPaneItem);
        gui.setItem(47, glassPaneItem);
        gui.setItem(49, glassPaneItem);
        gui.setItem(51, glassPaneItem);
        gui.setItem(52, glassPaneItem);

        // Metadata given to keep track of the opened inventories
        player.setMetadata("OpenedMainGUI", new FixedMetadataValue(OBGiveAll.getInstance(), gui));
        System.out.println("Metadata OpenedMainGUI given to player");

        player.openInventory(gui);
        System.out.println("MainGUI opened");
    }

    public static void kitCreationGUI(Player player) {
        // Creation of a gui of size 54 titled MainGUI
        Inventory gui = Bukkit.createInventory(player, 9 * 6, "KitCreationGUI");

        // Add items to the GUI
        // Exit gui item
        ItemStack exitItem = new ItemStack(Material.PAPER);
        ItemMeta metaExitItem = exitItem.getItemMeta();
        metaExitItem.setDisplayName(ChatColor.RED + "Exit");
        metaExitItem.setCustomModelData(10066);
        exitItem.setItemMeta(metaExitItem);
        // White stained-glass pane
        ItemStack glassPaneItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta metaGlassPaneItem = glassPaneItem.getItemMeta();
        metaGlassPaneItem.setDisplayName(" ");
        glassPaneItem.setItemMeta(metaGlassPaneItem);
        // Name kit
        ItemStack nameKitItem = new ItemStack(Material.OAK_SIGN);
        ItemMeta metaKitItem = nameKitItem.getItemMeta();
        metaKitItem.setDisplayName("Name of the kit");
        nameKitItem.setItemMeta(metaKitItem);
        // Save kit
        ItemStack saveKitItem = new ItemStack(Material.PAPER);
        ItemMeta metaSaveItem = saveKitItem.getItemMeta();
        metaSaveItem.setDisplayName("Save kit");
        metaSaveItem.setCustomModelData(10064);
        saveKitItem.setItemMeta(metaSaveItem);
        // Go back
        ItemStack goBackItem = new ItemStack(Material.PAPER);
        ItemMeta metaGoBackItem = goBackItem.getItemMeta();
        metaGoBackItem.setDisplayName("Back");
        metaGoBackItem.setCustomModelData(10078);
        goBackItem.setItemMeta(metaGoBackItem);

        // Set the different items on the gui
        // Set the position of the buttons in the gui
        gui.setItem(53, exitItem);
        gui.setItem(48, nameKitItem);
        gui.setItem(50, saveKitItem);
        gui.setItem(45, goBackItem);
        // Set the glass pane
        for(int i = 45 - 9; i < 45; i++) {
            gui.setItem(i, glassPaneItem);
        }
        gui.setItem(46, glassPaneItem);
        gui.setItem(47, glassPaneItem);
        gui.setItem(49, glassPaneItem);
        gui.setItem(51, glassPaneItem);
        gui.setItem(52, glassPaneItem);

        // Metadata given to keep track of the opened inventories
        player.setMetadata("OpenedKitCreationGUI", new FixedMetadataValue(OBGiveAll.getInstance(), gui));
        System.out.println("Metadata OpenedKitCreationGUI given to player");

        player.openInventory(gui);
        System.out.println("KitCreationGUI opened");
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        System.out.println("InventoryClickEvent triggered");
        Player player = (Player) event.getWhoClicked();

        if (player.hasMetadata("OpenedMainGUI")) {
            event.setCancelled(true);
            System.out.println("Cancelled event InventoryClickEvent for MainGUI");

            // Button to create a new kit
            ItemStack createItem = new ItemStack(Material.NETHER_STAR);
            ItemMeta metaCreateItem = createItem.getItemMeta();
            metaCreateItem.setDisplayName(ChatColor.GREEN + "Create a kit");
            createItem.setItemMeta(metaCreateItem);

            // Button to close the gui
            ItemStack exitItem = new ItemStack(Material.PAPER);
            ItemMeta metaExitItem = exitItem.getItemMeta();
            metaExitItem.setDisplayName(ChatColor.RED + "Exit");
            metaExitItem.setCustomModelData(10066);
            exitItem.setItemMeta(metaExitItem);

            // Get the clicked item and perform action
            if (event.getSlot() == 45 && event.getCurrentItem().isSimilar(createItem)) {
                System.out.println("Slot 45 of MainGUI clicked");
                kitCreationGUI(player);
            }

            // if the bedrock block is clicked, close the gui
            if (event.getSlot() == 53 && event.getCurrentItem().isSimilar(exitItem)) {
                System.out.println("Slot 53 of MainGUI clicked");
                player.closeInventory();
            }
        }

        if (player.hasMetadata("OpenedKitCreationGUI")) {

            // Button to close the gui
            ItemStack exitItem = new ItemStack(Material.PAPER);
            ItemMeta metaExitItem = exitItem.getItemMeta();
            metaExitItem.setDisplayName(ChatColor.RED + "Exit");
            metaExitItem.setCustomModelData(10066);
            exitItem.setItemMeta(metaExitItem);
            // Go back
            ItemStack goBackItem = new ItemStack(Material.PAPER);
            ItemMeta metaGoBackItem = goBackItem.getItemMeta();
            metaGoBackItem.setDisplayName("Back");
            metaGoBackItem.setCustomModelData(10078);
            goBackItem.setItemMeta(metaGoBackItem);

            int[] tabCancel = new int[14];
            for (int i = 0; i < 9; i++) {
                tabCancel[i] = 36 + i;
            }

            tabCancel[9] = 46;
            tabCancel[10] = 47;
            tabCancel[11] = 49;
            tabCancel[12] = 51;
            tabCancel[13] = 52;

            for (int i = 0; i < 14; i++) {
                if (event.getSlot() == tabCancel[i]){
                    event.setCancelled(true);
                    System.out.println("Cancelled event InventoryClickEvent for KitCreationGUI");

                    System.out.println("White stained-glass pane of KitCreationGUI clicked");
                    break;
                }
            }

            switch (event.getSlot()) {
                // Cannot click on any blocks of the last line
                case 45:
                    event.setCancelled(true);
                    System.out.println("Cancelled event InventoryClickEvent for KitCreationGUI");

                    // if the slot contains the correct item, opens the mainGUI
                    if (event.getCurrentItem().isSimilar(goBackItem))
                    {
                        System.out.println("Slot 45 of KitCreationGUI clicked");
                        mainGUI(player);
                    }
                    break;
                case 48:
                    event.setCancelled(true);
                    System.out.println("Cancelled event InventoryClickEvent for KitCreationGUI");

                    System.out.println("Slot 48 of KitCreationGUI clicked");
                    break;
                case 50:
                    event.setCancelled(true);
                    System.out.println("Cancelled event InventoryClickEvent for KitCreationGUI");

                    System.out.println("Slot 50 of KitCreationGUI clicked");
                    break;
                case 53:
                    // if the bedrock block is clicked, cancel this action and close the gui
                    if (event.getCurrentItem().isSimilar(exitItem)) {
                        event.setCancelled(true);
                        System.out.println("Cancelled event InventoryClickEvent for KitCreationGUI");

                        System.out.println("Slot 53 of KitCreationGUI clicked");
                        player.closeInventory();
                    }
                    break;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        System.out.println("InventoryCloseEvent triggered");
        Player player = (Player) event.getPlayer();

        if (player.hasMetadata("OpenedMainGUI")) {
            player.removeMetadata("OpenedMainGUI", OBGiveAll.getInstance());
            System.out.println("Metadata OpenedMainGUI removed from player");
        } else {
            if (player.hasMetadata("OpenedKitCreationGUI")) {
                player.removeMetadata("OpenedKitCreationGUI", OBGiveAll.getInstance());
                System.out.println("Metadata OpenedKitCreationGUI removed from player");
            }
        }
    }
}

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
        // Creation of a gui of size 27 titled MainGUI
        Inventory gui = Bukkit.createInventory(player, 9*4, "MainGUI");

        // Add items to the GUI
        // Creation kit item named "createItem"
        ItemStack createItem = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta metacreateItem = createItem.getItemMeta();
        metacreateItem.setDisplayName(ChatColor.GREEN + "Create a kit");
        createItem.setItemMeta(metacreateItem);
        // Exit gui item named "exitItem"
        ItemStack exitItem = new ItemStack(Material.BEDROCK);
        ItemMeta metaExitItem = exitItem.getItemMeta();
        metaExitItem.setDisplayName(ChatColor.RED + "Exit");
        exitItem.setItemMeta(metaExitItem);

        // Set the position of the buttons in the gui
        gui.setItem(0, createItem);
        gui.setItem(35, exitItem);

        // Metadata given to keep track of the opened inventories
        player.setMetadata("OpenedMainGUI", new FixedMetadataValue(OBGiveAll.getInstance(), gui));
        System.out.println("Metadata OpenedMainGUI given to player");

        player.openInventory(gui);
        System.out.println("MainGUI opened");
    }

    public static void kitCreationGUI(Player player) {
        // Creation of a gui of size 27 titled MainGUI
        Inventory gui = Bukkit.createInventory(player, 9*4, "KitCreationGUI");

        // Add items to the GUI
        // Exit gui item
        ItemStack exitItem = new ItemStack(Material.BEDROCK);
        ItemMeta metaExitItem = exitItem.getItemMeta();
        metaExitItem.setDisplayName(ChatColor.RED + "Exit");
        exitItem.setItemMeta(metaExitItem);

        // Set the position of the buttons in the gui
        gui.setItem(35, exitItem);

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

        if(player.hasMetadata("OpenedMainGUI")){
            event.setCancelled(true);
            System.out.println("Cancelled event InventoryClickEvent for MainGUI");

            // Button to create a new kit
            ItemStack createItem = new ItemStack(Material.DIAMOND_PICKAXE);
            ItemMeta metacreateItem = createItem.getItemMeta();
            metacreateItem.setDisplayName(ChatColor.GREEN + "Create a kit");
            createItem.setItemMeta(metacreateItem);

            // Button to close the gui
            ItemStack exitItem = new ItemStack(Material.BEDROCK);
            ItemMeta metaExitItem = exitItem.getItemMeta();
            metaExitItem.setDisplayName(ChatColor.RED + "Exit");
            exitItem.setItemMeta(metaExitItem);

            // Get the clicked item and perform action
            if(event.getSlot() == 0 && event.getCurrentItem().isSimilar(createItem)) {
                System.out.println("Slot 0 of MainGUI clicked");
                kitCreationGUI(player);
            }

            // if the bedrock block is clicked, close the gui
            if(event.getSlot() == 35 && event.getCurrentItem().isSimilar(exitItem)) {
                System.out.println("Slot 35 of MainGUI clicked");
                player.closeInventory();
            }
        }

        if (player.hasMetadata("OpenedKitCreationGUI")) {

            // Button to close the gui
            ItemStack exitItem = new ItemStack(Material.BEDROCK);
            ItemMeta metaExitItem = exitItem.getItemMeta();
            metaExitItem.setDisplayName(ChatColor.RED + "Exit");
            exitItem.setItemMeta(metaExitItem);

            // if the bedrock block is clicked, close the gui
            if(event.getSlot() == 35 && event.getCurrentItem().isSimilar(exitItem)) {
                event.setCancelled(true);
                System.out.println("Cancelled event InventoryClickEvent for KitCreationGUI");

                System.out.println("Slot 35 of KitCreationGUI clicked");
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        System.out.println("InventoryCloseEvent triggered");
        Player player = (Player) event.getPlayer();

        if(player.hasMetadata("OpenedMainGUI")) {
            player.removeMetadata("OpenedMainGUI", OBGiveAll.getInstance());
            System.out.println("Metadata OpenedMainGUI removed from player");
        }
        else {
            if(player.hasMetadata("OpenedKitCreationGUI")) {
                player.removeMetadata("OpenedKitCreationGUI", OBGiveAll.getInstance());
                System.out.println("Metadata OpenedKitCreationGUI removed from player");
            }
        }
    }
}

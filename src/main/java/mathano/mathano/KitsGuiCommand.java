package mathano.mathano;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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

    private static int taille;


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

        new BukkitRunnable() {
            @Override
            public void run() {
                // Metadata given to keep track of the opened inventories
                player.setMetadata("OpenedMainGUI7650", new FixedMetadataValue(OBGiveAll.getInstance(), gui));
                System.out.println("Metadata OpenedMainGUI given to player");
            }
        }.runTaskLater(OBGiveAll.getInstance(), 1);

        player.openInventory(gui);
        System.out.println("MainGUI opened");
    }

    public static void kitCreationGUI(Player player) {
        // Creation of a gui of size 54 titled MainGUI
        Inventory gui = Bukkit.createInventory(player, 9 * 6, "KitCreationGUI");

        // Set the different items on the gui
        // Set the position of the buttons in the gui
        gui.setItem(53, exitItem);
        gui.setItem(49, saveKitItem);
        gui.setItem(45, goBackItem);
        // Set the glass pane
        for (int i = 45 - 9; i < 45; i++) {
            gui.setItem(i, glassPaneItem);
        }
        gui.setItem(46, glassPaneItem);
        gui.setItem(47, glassPaneItem);
        gui.setItem(48, glassPaneItem);
        gui.setItem(50, glassPaneItem);
        gui.setItem(51, glassPaneItem);
        gui.setItem(52, glassPaneItem);


        new BukkitRunnable() {
            @Override
            public void run() {
                // Metadata given to keep track of the opened inventories
                player.setMetadata("OpenedKitCreationGUI", new FixedMetadataValue(OBGiveAll.getInstance(), gui));
                System.out.println("Metadata OpenedKitCreationGUI given to player");
            }
        }.runTaskLater(OBGiveAll.getInstance(), 1);

        player.openInventory(gui);
        System.out.println("KitCreationGUI opened");
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (player.hasMetadata("OpenedMainGUI7650")) {
            event.setCancelled(true);
            System.out.println("Cancelled event InventoryClickEvent for MainGUI");

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
            if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(glassPaneItem)) {
                event.setCancelled(true);
                System.out.println("Cancelled event InventoryClickEvent for KitCreationGUI");

                System.out.println("White stained-glass pane of KitCreationGUI clicked");
            }

            switch (event.getSlot()) {
                // Cannot click on any blocks of the last line
                case 45:
                    event.setCancelled(true);
                    System.out.println("Cancelled event InventoryClickEvent for KitCreationGUI");

                    // if the slot contains the correct item, opens the mainGUI
                    if (event.getCurrentItem().isSimilar(goBackItem)) {
                        System.out.println("Slot 45 of KitCreationGUI clicked");
                        mainGUI(player);
                    }
                    break;
                case 49:
                    event.setCancelled(true);
                    System.out.println("Cancelled event InventoryClickEvent for KitCreationGUI");

                    System.out.println("Slot 49 of KitCreationGUI clicked");

                    taille = 0;
                    for (int i = 0; i < 36; i++) {
                        if (event.getClickedInventory().getItem(i) != null) {
                            taille++;
                        }
                    }

                    if (taille > 0) {
                        new AnvilGUI.Builder()
                                .onClick((slot, stateSnapshot) -> { // Either use sync or async variant, not both
                                    if (slot != AnvilGUI.Slot.OUTPUT) {
                                        return Collections.emptyList();
                                    }

                                    if (!stateSnapshot.getText().equalsIgnoreCase("") && !stateSnapshot.getText().equalsIgnoreCase("Nom du kit")) {
                                        saveKit(event.getClickedInventory(), player, stateSnapshot.getText(), taille);
                                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                                    } else {
                                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Nom du kit"));
                                    }
                                })
                                .text("Nom du kit")              //sets the text the GUI should start with
                                .title("Nom du kit")             //set the title of the GUI (only works in 1.14+)
                                .plugin(OBGiveAll.getInstance()) //set the plugin instance
                                .open(player);
                    }
                    break;
                case 53:
                    event.setCancelled(true);
                    System.out.println("Cancelled event InventoryClickEvent for KitCreationGUI");

                    if (event.getCurrentItem().isSimilar(exitItem)) {
                        System.out.println("Slot 53 of KitCreationGUI clicked");
                        player.closeInventory();
                    }
                    break;
            }
        }
    }

    public void saveKit(Inventory kit, Player player, String nom, int taille) {
        // init variables
        ItemStack[] items = new ItemStack[taille];

        int cmp = 0;

        for (int i = 0; i < 36; i++) {
            if (kit.getItem(i) != null) {
                items[cmp] = kit.getItem(i);
                cmp++;
            }
        }

        FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();

        if (dataKits.contains(nom)) {
            dataKits.set(nom, null);
        }

        for (int i = 0; i < cmp; i++) {
            dataKits.set(nom + "." + i, kit.getItem(i));
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
            if (player.hasMetadata("OpenedKitCreationGUI")) {
                player.removeMetadata("OpenedKitCreationGUI", OBGiveAll.getInstance());
                System.out.println("Metadata OpenedKitCreationGUI removed from player");
            }
        }
    }
}

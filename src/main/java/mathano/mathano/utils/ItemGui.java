package mathano.mathano.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemGui {
    // Init of the various buttons as attributes because they are used in different Guis
    // Button to create a kit
    public static final ItemStack createItem = new ItemStack(Material.NETHER_STAR);
    public static final ItemMeta metaCreateItem = createItem.getItemMeta();

    // Button to exit the GUI
    public static final ItemStack exitItem = new ItemStack(Material.PAPER);
    public static final ItemMeta metaExitItem = exitItem.getItemMeta();

    // Delimitation of the GUI
    public static final ItemStack glassPaneItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
    public static final ItemMeta metaGlassPaneItem = glassPaneItem.getItemMeta();


    // Button to save the kit
    public static final ItemStack saveKitItem = new ItemStack(Material.PAPER);
    public static final ItemMeta metaSaveItem = saveKitItem.getItemMeta();

    // Button to go back in mainGUI
    public static final ItemStack goBackItem = new ItemStack(Material.PAPER);
    public static final ItemMeta metaGoBackItem = goBackItem.getItemMeta();

    public static final ItemStack deleteKitItem = new ItemStack(Material.TNT);
    public static final ItemMeta metaDeleteKitItem = deleteKitItem.getItemMeta();

    public static final ItemStack leftItem = new ItemStack(Material.PAPER);
    public static final ItemMeta metaLeftItem = leftItem.getItemMeta();

    public static final ItemStack rightItem = new ItemStack(Material.PAPER);
    public static final ItemMeta metaRightItem = rightItem.getItemMeta();

    public static final ItemStack confirmItem = new ItemStack(Material.PAPER);
    public static final ItemMeta metaConfirmItem = confirmItem.getItemMeta();

    public ItemGui() {
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
    }
}

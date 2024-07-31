package mathano.mathano.handlers;

import com.google.gson.Gson;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import dev.triumphteam.gui.guis.StorageGui;
import mathano.mathano.OBGiveAll;
import mathano.mathano.database.jsondata.DataKitsJson;
import mathano.mathano.database.jsondata.ItemMetaJson;
import mathano.mathano.database.jsondata.ItemStackJson;
import mathano.mathano.enums.Placeholders;
import mathano.mathano.managers.DataKitsManager;
import mathano.mathano.managers.JsonManager;
import mathano.mathano.managers.RewardsManager;
import mathano.mathano.utils.ItemGui;
import mathano.mathano.utils.Utils;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class KitsGui {
    private static String section = "kitsGui";
    public static KitsGui INSTANCE;

    public KitsGui() {
        INSTANCE = this;
    }

    // Gui that shows every created kits
    public void mainGui(Player player) {
        // Creation of the main interface
        PaginatedGui mainGui = Gui.paginated()
                .title(Component.text("MainGUI"))
                .rows(6)
                .pageSize(36)
                .disableAllInteractions()
                .create();

        // Creation of the buttons
        GuiItem createGuiItem = ItemBuilder.from(ItemGui.createItem).asGuiItem(inventoryClickEvent -> {
            kitCreationGUI(player);
        });
        GuiItem exitGuiItem = ItemBuilder.from(ItemGui.exitItem).asGuiItem(inventoryClickEvent -> {
            mainGui.close(player);
        });
        GuiItem leftGuiItem = ItemBuilder.from(ItemGui.leftItem).asGuiItem(inventoryClickEvent -> {
            mainGui.previous();
        });
        GuiItem rightGuiItem = ItemBuilder.from(ItemGui.rightItem).asGuiItem(inventoryClickEvent -> {
            mainGui.next();
        });
        GuiItem glassPaneItemGui = ItemBuilder.from(ItemGui.glassPaneItem).asGuiItem();

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


        ItemStack icon;
        ItemMeta iconMeta;

        for (String key : DataKitsManager.DATA_KITS_CONFIG.getKeys(false)) {
            //We are getting every key from our config.yml file
            ConfigurationSection section = DataKitsManager.DATA_KITS_CONFIG.getConfigurationSection(key);
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
    public void kitCreationGUI(Player player) {
        // Creation of the kit creation interface
        Gui kitCreationGui = Gui.gui()
                .title(Component.text("KitCreationGUI"))
                .rows(6)
                .create();

        // Creation of the buttons
        GuiItem goBackGuiItem = ItemBuilder.from(ItemGui.goBackItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            mainGui(player);
        });
        GuiItem saveKitGuiItem = ItemBuilder.from(ItemGui.saveKitItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            int length = 0;
            for (int i = 0; i < 36; i++) {
                if (inventoryClickEvent.getClickedInventory().getItem(i) != null) {
                    length++;
                }
            }

            if (length > 0) {
                int finalLength = length;
                new AnvilGUI.Builder()
                        // Either use sync or async variant, not both
                        .onClick((slot, stateSnapshot) -> {
                            if (slot != AnvilGUI.Slot.OUTPUT) {
                                return Collections.emptyList();
                            }

                            return verifKitName(stateSnapshot.getText(), null, inventoryClickEvent.getClickedInventory(), player, false, finalLength);
                        })
                        // Sets the text the GUI should start with
                        .text("Nom du kit")
                        // Set the title of the GUI (only works in 1.14+)
                        .title("Nom du kit")
                        // Set the plugin instance
                        .plugin(OBGiveAll.INSTANCE)
                        .open(player);
            }
        });
        GuiItem exitGuiItem = ItemBuilder.from(ItemGui.exitItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            kitCreationGui.close(player);
        });
        GuiItem glassPaneItemGui = ItemBuilder.from(ItemGui.glassPaneItem).asGuiItem(inventoryClickEvent -> {
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
    public void kitEditGUI(Player player, String name) {
        StorageGui kitEditGui = Gui.storage()
                .title(Component.text("KitEditGUI"))
                .rows(6)
                .create();

        // Creation of the buttons
        GuiItem goBackGuiItem = ItemBuilder.from(ItemGui.goBackItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            mainGui(player);
        });
        GuiItem saveKitGuiItem = ItemBuilder.from(ItemGui.saveKitItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            int length = 0;
            for (int i = 0; i < 36; i++) {
                if (inventoryClickEvent.getClickedInventory().getItem(i) != null) {
                    length++;
                }
            }

            if (length > 0) {
                int finalLength = length;
                new AnvilGUI.Builder()
                        // Either use sync or async variant, not both
                        .onClick((slot, stateSnapshot) -> {
                            if (slot != AnvilGUI.Slot.OUTPUT) {
                                return Collections.emptyList();
                            }

                            return verifKitName(stateSnapshot.getText(), name, inventoryClickEvent.getClickedInventory(), player, true, finalLength);
                        })
                        // Sets the text the GUI should start with
                        .text(name)
                        // Set the title of the GUI (only works in 1.14+)
                        .title("Nom du kit")
                        // Set the plugin instance
                        .plugin(OBGiveAll.INSTANCE)
                        .open(player);
            }
        });
        GuiItem exitGuiItem = ItemBuilder.from(ItemGui.exitItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            kitEditGui.close(player);
        });
        GuiItem glassPaneItemGui = ItemBuilder.from(ItemGui.glassPaneItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
        });
        GuiItem deleteKitItemGui = ItemBuilder.from(ItemGui.deleteKitItem).asGuiItem(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);


            Gui validateDeleteGui = Gui.gui()
                    .title(Component.text("Confirmer la suppression"))
                    .rows(1)
                    .create();

            GuiItem confirmItemGui = ItemBuilder.from(ItemGui.confirmItem).asGuiItem(inventoryClickEvent1 -> {
                deleteKit(inventoryClickEvent.getClickedInventory().getItem(47).getItemMeta().getDisplayName(), player);
                mainGui(player);
            });

            GuiItem cancelItemGui = ItemBuilder.from(ItemGui.exitItem).asGuiItem(inventoryClickEvent1 -> {
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


        ConfigurationSection section = DataKitsManager.DATA_KITS_CONFIG.getConfigurationSection(name);

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
    public static void saveKit(Inventory kit, Player player, String name, String oldName, int length) {
        // init variables
        ItemStack[] items = new ItemStack[length];
        ItemStack icon;
        ItemMeta iconMeta;
        int cmp = 0;
        boolean edit = false;

        if (kit.getItem(47) != null) {
            icon = kit.getItem(47);
        } else {
            icon = new ItemStack(Material.CHEST);
        }

        iconMeta = icon.getItemMeta();
        iconMeta.setDisplayName(name);
        icon.setItemMeta(iconMeta);

        if (DataKitsManager.DATA_KITS_CONFIG.contains(name)) {
            DataKitsManager.DATA_KITS_CONFIG.set(name, null);
            edit = true;
        }

        if (oldName != null && DataKitsManager.DATA_KITS_CONFIG.contains(oldName)) {
            DataKitsManager.DATA_KITS_CONFIG.set(oldName, null);

            for (String key : RewardsManager.REWARDS_CONFIG.getKeys(false)) {
                ConfigurationSection section = RewardsManager.REWARDS_CONFIG.getConfigurationSection(key);

                if (section.contains(oldName)) {
                    int numberKits = section.getInt(oldName);
                    section.set(name, numberKits);

                    section.set(oldName, null);
                    if (section.getKeys(false).size() <= 0) {
                        RewardsManager.REWARDS_CONFIG.set(player.getUniqueId().toString(), null);
                    }
                }
            }

            edit = true;
        }

        DataKitsManager.DATA_KITS_CONFIG.set(name + ".name", icon);

        for (int i = 0; i < 36; i++) {
            if (kit.getItem(i) != null) {
                items[cmp] = kit.getItem(i);
                cmp++;
            }
        }

        for (int i = 0; i < cmp; i++) {
            DataKitsManager.DATA_KITS_CONFIG.set(name + "." + i, items[i]);
        }

        if (edit) {
            // Message sent whenever a kit was successfully modified
            player.sendMessage(Utils.getText(section, "kitModified", Placeholders.KIT_NAME.set(name)));

        } else {
            // Message sent whenever a kit was successfully created
            player.sendMessage(Utils.getText(section, "kitCreated", Placeholders.KIT_NAME.set(name)));
        }

        DataKitsManager.INSTANCE.save();
    }

    // Deletes the selected kit from the DataKits and Rewards cache
    public static void deleteKit(String name, Player player) {
        DataKitsManager.DATA_KITS_CONFIG.set(name, null);
        player.sendMessage(ChatColor.GREEN + "Le kit " + name + " a été supprimé !");

        for (String key : RewardsManager.REWARDS_CONFIG.getKeys(false)) {
            ConfigurationSection section = RewardsManager.REWARDS_CONFIG.getConfigurationSection(key);

            if (section.contains(name)) {
                section.set(name, null);
                if (section.getKeys(false).size() <= 0) {
                    RewardsManager.REWARDS_CONFIG.set(player.getUniqueId().toString(), null);
                }
            }
        }
    }

    public List<AnvilGUI.ResponseAction> verifKitName(String newKitName, String oldKitName, Inventory clickedInventory, Player player, Boolean edit, int length) {
        if (!newKitName.equalsIgnoreCase("")
                && !newKitName.contains(" ")) {
            if (!edit) {
                if (DataKitsManager.DATA_KITS_CONFIG.contains(newKitName)) {
                    // Message sent when the saved kit already exists
                    player.sendMessage(Utils.getText(section, "alreadyExists", Placeholders.KIT_NAME.set(newKitName)));
                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Nom du kit"));
                } else {
                    //saveKit(clickedInventory, player, newKitName, null, length);
                    saveKit2(clickedInventory, player, newKitName, null);
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                }
            } else {
                if (DataKitsManager.DATA_KITS_CONFIG.contains(newKitName) && newKitName.equals(oldKitName)) {
                    saveKit(clickedInventory, player, newKitName, DataKitsManager.DATA_KITS_CONFIG.getConfigurationSection(oldKitName).getItemStack("name").getItemMeta().getDisplayName(), length);
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                }

                if (!DataKitsManager.DATA_KITS_CONFIG.contains(newKitName)) {
                    saveKit(clickedInventory, player, newKitName, DataKitsManager.DATA_KITS_CONFIG.getConfigurationSection(oldKitName).getItemStack("name").getItemMeta().getDisplayName(), length);
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                } else {
                    player.sendMessage(ChatColor.RED + "Le kit " + newKitName + " existe déjà !");
                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText(oldKitName));
                }
            }
        } else {
            if (newKitName.contains(" ")) {
                // Message sent when a space was put in the kit name
                player.sendMessage(Utils.getText(section, "spaceInName"));
            }
            return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Nom du kit"));
        }
    }

    /*private void saveKit2(Inventory kit, Player player, String name, String oldName) {
        ItemStack icon = kit.getItem(47);
        ItemStackJson iconJson = new ItemStackJson();

        if (icon == null) {
            icon = new ItemStack(Material.CHEST);
        }
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setDisplayName(name);

        icon.setItemMeta(iconMeta);

        iconJson.setMaterial(icon.getType());
        iconJson.setAmount(icon.getAmount());
        //iconJson.setMeta(createItemMetaJson(icon));
        iconJson.setMeta(icon.getItemMeta());

        List<ItemStackJson> itemStackJsonList = new ArrayList<>();

        getKitContent(kit).forEach(itemStack -> {
            ItemStackJson item = new ItemStackJson();
            //item.setMeta(createItemMetaJson(itemStack));
            item.setMeta(itemStack.getItemMeta());
            item.setMaterial(itemStack.getType());
            item.setAmount(itemStack.getAmount());
            itemStackJsonList.add(item);
        });

        DataKitsJson dataKitsJson = new DataKitsJson();
        dataKitsJson.setIcon(iconJson);
        dataKitsJson.setItems(itemStackJsonList);
        dataKitsJson.setName(name);

        OBGiveAll.INSTANCE.getLogger().info(JsonManager.INSTANCE.createJsonKit(dataKitsJson));
    }*/

    private List<ItemStack> getKitContent(Inventory kit) {
        List<ItemStack> items = new ArrayList<>();
        ItemStack item;

        for (int i = 0; i < 36; i++) {
            item = kit.getItem(i);
            if(item == null)continue;

            items.add(item);
        }
        return items;
    }

    private ItemMetaJson createItemMetaJson(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        ItemMetaJson itemMetaJson = new ItemMetaJson();

        if (itemMeta.getItemFlags().isEmpty()) {
            itemMetaJson.setItemFlag(itemMeta.getItemFlags());
        }

        if (itemMeta.hasLore()) {
            itemMetaJson.setLore(itemMeta.getLore());
        }
        if(itemMeta.hasEnchants()) {
            itemMetaJson.setEnchantments(itemMeta.getEnchants());
        }

        if (itemMeta.hasDisplayName()) {
            itemMetaJson.setDisplayName(itemMeta.getDisplayName());
        }

        if (itemMeta.hasCustomModelData()) {
            itemMetaJson.setCustomModelData(itemMeta.getCustomModelData());
        }

        if (!itemMeta.getPersistentDataContainer().isEmpty()) {
            itemMetaJson.setPersistentDataContainer(itemMeta.getPersistentDataContainer());
        }

        return itemMetaJson;
    }

    private void saveKit2(Inventory kit, Player player, String name, String oldName) {
        ItemStack icon = kit.getItem(47);
        //ItemStackJson iconJson = new ItemStackJson();
        Gson gson = new Gson();
        String iconJson;

        if (icon == null) {
            icon = new ItemStack(Material.CHEST);
        }
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setDisplayName(name);

        icon.setItemMeta(iconMeta);

        //iconJson.setItemStack(icon);
        iconJson = serializeAndEncodeItemStack(icon);

        //List<ItemStackJson> itemStackJsonList = new ArrayList<>();
        List<String> itemStackJsonList = new ArrayList<>();

        getKitContent(kit).forEach(itemStack -> {
            //ItemStackJson item = new ItemStackJson();
            //item.setItemStack(itemStack);
            //itemStackJsonList.add(item);
            itemStackJsonList.add(serializeAndEncodeItemStack(itemStack));
        });

        DataKitsJson dataKitsJson = new DataKitsJson();
        dataKitsJson.setIcon(iconJson);
        dataKitsJson.setItems(itemStackJsonList);
        dataKitsJson.setName(name);

        OBGiveAll.INSTANCE.getLogger().info(JsonManager.INSTANCE.createJsonKit(dataKitsJson));

        OBGiveAll.INSTANCE.getLogger().info("");

        try {
            OBGiveAll.INSTANCE.getLogger().info(deserialize(JsonManager.INSTANCE.reader.readValue(JsonManager.INSTANCE.createJsonKit(dataKitsJson), DataKitsJson.class).getIcon()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            player.getInventory().addItem(deserializeAndDecodeItemStack(JsonManager.INSTANCE.reader.readValue(JsonManager.INSTANCE.createJsonKit(dataKitsJson), DataKitsJson.class).getIcon()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String serializeAndEncodeItemStack(ItemStack item){
        try{
            //Serialize the item(turn it into byte stream)
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
            os.writeObject(item);
            os.flush();

            byte[] serializedObject = io.toByteArray();
            return new String(Base64.getEncoder().encode(serializedObject));
        }catch (IOException ex){
            System.out.println(ex);
        }
        return "";
    }


    public ItemStack deserializeAndDecodeItemStack(String encodedObject){
        try{
            byte[] serializedObject = Base64.getDecoder().decode(encodedObject);
            ByteArrayInputStream in = new ByteArrayInputStream(serializedObject);
            BukkitObjectInputStream is = new BukkitObjectInputStream(in);

            return (ItemStack) is.readObject();
        }catch (IOException | ClassNotFoundException ex){
            System.out.println(ex);
        }
        return null;
    }

    public String deserialize(String encodedObject){
        byte[] serializedObject = Base64.getDecoder().decode(encodedObject);
        return Arrays.toString(serializedObject);
    }
}
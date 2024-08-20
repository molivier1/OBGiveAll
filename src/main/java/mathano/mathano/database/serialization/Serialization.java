package mathano.mathano.database.serialization;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Serialization {
    public static Serialization INSTANCE;

    public Serialization() {
        INSTANCE = this;
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

    public List<ItemStack> deserializeAndDecodeItemStackList(List<String> encodedObjectList) {
        List<ItemStack> itemStackList = new ArrayList<>();

        try {
            for (String encodedObject : encodedObjectList) {
                byte[] serializedObject = Base64.getDecoder().decode(encodedObject);
                ByteArrayInputStream in = new ByteArrayInputStream(serializedObject);
                BukkitObjectInputStream is = new BukkitObjectInputStream(in);

                itemStackList.add((ItemStack) is.readObject());
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return itemStackList;
    }
}

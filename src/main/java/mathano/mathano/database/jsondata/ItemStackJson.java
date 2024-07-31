package mathano.mathano.database.jsondata;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@NoArgsConstructor
@Data
public class ItemStackJson {
    //private Integer amount;
    //private Material material;
    //private ItemMeta meta;
    private ItemStack itemStack;
}

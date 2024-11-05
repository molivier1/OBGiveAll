package mathano.mathano.database;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@NoArgsConstructor
@Data
public class DataKits {
    private String name;
    private ItemStack icon;
    private List<ItemStack> items;
    private String command;
}

package mathano.mathano.database.jsondata;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@Data
public class ItemMetaJson {
    private String displayName;
    private Integer customModelData;
    private Map<Enchantment, Integer> enchantments;
    private List<String> lore;
    private PersistentDataContainer persistentDataContainer;
    private Set<ItemFlag> itemFlag;
}

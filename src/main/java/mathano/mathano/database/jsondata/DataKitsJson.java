package mathano.mathano.database.jsondata;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DataKitsJson {
    //private ItemStackJson icon;
    private String icon;
    //private List<ItemStackJson> items;
    private List<String> items;
    private String name;
}

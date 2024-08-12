package mathano.mathano.database.jsondata;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DataKitsJson {
    private String name;
    private String icon;
    private List<String> items;
}

package mathano.mathano.database.jsondata;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class CommandsJson {
    private List<String> commands;
}

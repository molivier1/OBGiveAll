package mathano.mathano.database.jsondata;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.UUID;

@NoArgsConstructor
@Data
public class RewardsJson {
    private UUID player_uuid;
    private HashMap<String, Integer> rewards;
}

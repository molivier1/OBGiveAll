package mathano.mathano.database;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@NoArgsConstructor
@Data
public class Logs {
    private UUID player_uuid;
    private String kit_name;
    private Timestamp timestamp;
}

package mathano.mathano.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import mathano.mathano.database.jsondata.DataKitsJson;

public class JsonManager {
    public ObjectMapper mapper = new JsonMapper();
    public ObjectWriter writer = mapper.writer();
    public ObjectReader reader = mapper.reader();
    public static JsonManager INSTANCE;

    public JsonManager() {
        INSTANCE = this;

        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        mapper.writerWithDefaultPrettyPrinter();
    }

    public String createJsonKit(DataKitsJson dataKitsJson) {
        try {
            return mapper.writeValueAsString(dataKitsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}

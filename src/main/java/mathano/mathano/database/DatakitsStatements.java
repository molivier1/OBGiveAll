package mathano.mathano.database;

import mathano.mathano.database.jsondata.DataKitsJson;

import java.util.UUID;

public class DatakitsStatements {
    public static final String CREATE_DATAKITS_TABLE = "CREATE TABLE IF NOT EXISTS datakits ("
            + "uuid             VARCHAR(36)         NOT NULL PRIMARY KEY,"
            + "kit_name         VARCHAR(30)         NOT NULL,"
            + "content          JSON                NOT NULL);";

    public static String ADD_KIT = "INSERT INTO datakits VALUES (?, ?, ?);";

    public static String LOAD_KIT_CACHE = "SELECT kit_name, content FROM datakits;";

    public static String GET_KIT = "SELECT content FROM datakits WHERE kit_name = ?;";
}

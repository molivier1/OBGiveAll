package mathano.mathano.database;

public class DatakitsStatements {
    public static final String CREATE_DATAKITS_TABLE = "CREATE TABLE IF NOT EXISTS datakits ("
            + "uuid             VARCHAR(30)         NOT NULL PRIMARY KEY,"
            + "kit_name         VARCHAR(30)         NOT NULL,"
            + "content          JSON                NOT NULL);";
}

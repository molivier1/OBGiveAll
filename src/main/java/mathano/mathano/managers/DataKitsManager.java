package mathano.mathano.managers;

import mathano.mathano.database.DataKits;
import mathano.mathano.database.jsondata.DataKitsJson;
import mathano.mathano.database.serialization.Serialization;
import mathano.mathano.database.statements.DatakitsStatements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DataKitsManager {
    public static DataKitsManager INSTANCE;

    public static Map<String, String> kitJSONCache = new HashMap<>();

    public static Map<String, DataKits> dataKits = new HashMap<>();

    public DataKitsManager() {
        INSTANCE = this;
    }

    public void loadKitsIntoCache() {
        try(Connection connection = DatabaseManager.INSTANCE.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DatakitsStatements.LOAD_KIT_CACHE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String kitName = resultSet.getString("kit_name");
                String content = resultSet.getString("content");

                kitJSONCache.put(kitName, content);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        kitJSONCache.forEach((kitName, content) -> {
            DataKits data = new DataKits();
            data.setName(kitName);
            try {
                data.setIcon(Serialization.INSTANCE.deserializeAndDecodeItemStack(JsonManager.INSTANCE.reader.readValue(content, DataKitsJson.class).getIcon()));
                data.setItems(Serialization.INSTANCE.deserializeAndDecodeItemStackList(JsonManager.INSTANCE.reader.readValue(content, DataKitsJson.class).getItems()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            dataKits.put(kitName, data);
        });
    }

    public void saveKitsFromCache() {
        try(Connection connection = DatabaseManager.INSTANCE.getConnection()) {
            PreparedStatement insertStatement = connection.prepareStatement(DatakitsStatements.INSERT_KIT);
            PreparedStatement updateStatement = connection.prepareStatement(DatakitsStatements.UPDATE_KIT);
            PreparedStatement selectContentStatement = connection.prepareStatement(DatakitsStatements.SELECT_CONTENT);
            PreparedStatement deleteStatement = connection.prepareStatement(DatakitsStatements.DELETE_KIT);

            for (Map.Entry<String, DataKits> entry : dataKits.entrySet()) {
                String kitName = entry.getKey();
                DataKits datakit = entry.getValue();

                DataKitsJson dataKitsJson = new DataKitsJson();
                dataKitsJson.setName(kitName);
                dataKitsJson.setIcon(Serialization.INSTANCE.serializeAndEncodeItemStack(datakit.getIcon()));

                List<String> itemStackJsonList = new ArrayList<>();
                datakit.getItems().forEach(itemStack -> {
                    itemStackJsonList.add(Serialization.INSTANCE.serializeAndEncodeItemStack(itemStack));
                });

                dataKitsJson.setItems(itemStackJsonList);

                String content = JsonManager.INSTANCE.createJsonKit(dataKitsJson);

                if (kitExistsAndChangedInDatabase(kitName, content, selectContentStatement)) {
                    updateStatement.setString(1, content);
                    updateStatement.setString(2, kitName);
                    updateStatement.executeUpdate();

                } else if (!kitExistsInDatabase(kitName, selectContentStatement)) {
                    insertStatement.setString(1, UUID.randomUUID().toString());
                    insertStatement.setString(2, kitName);
                    insertStatement.setString(3, content);
                    insertStatement.executeUpdate();
                }
            }

            deleteKitsNotInCache(connection, deleteStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean kitExistsAndChangedInDatabase(String kitName, String newContent, PreparedStatement selectContentStatement) throws SQLException {
        selectContentStatement.setString(1, kitName);
        ResultSet resultSet = selectContentStatement.executeQuery();

        if (resultSet.next()) {
            String existingContent = resultSet.getString("content");
            return !existingContent.equals(newContent);
        }

        return false;
    }

    private boolean kitExistsInDatabase(String kitName, PreparedStatement selectContentStatement) throws SQLException {
        selectContentStatement.setString(1, kitName);
        ResultSet resultSet = selectContentStatement.executeQuery();
        return resultSet.next();
    }

    private void deleteKitsNotInCache(Connection connection, PreparedStatement deleteStatement) throws SQLException {
        PreparedStatement selectStatement = connection.prepareStatement(DatakitsStatements.SELECT_KIT_NAME);
        ResultSet resultSet = selectStatement.executeQuery();

        while (resultSet.next()) {
            String kitName = resultSet.getString("kit_name");
            if (!dataKits.containsKey(kitName)) {
                deleteStatement.setString(1, kitName);
                deleteStatement.executeUpdate();
            }
        }
    }
}

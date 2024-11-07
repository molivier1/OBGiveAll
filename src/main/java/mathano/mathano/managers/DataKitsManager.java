package mathano.mathano.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import mathano.mathano.database.DataKits;
import mathano.mathano.database.jsondata.CommandsJson;
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

    public CommandsJson getCommands(String stringCommandsJson) {
        try {
            JsonNode jsonNode = JsonManager.INSTANCE.mapper.readTree(stringCommandsJson);

            if(jsonNode.isArray()) {
                CommandsJson commandsJson = new CommandsJson();
                commandsJson.setCommands(JsonManager.INSTANCE.mapper.convertValue(jsonNode, List.class));

                return commandsJson;
            }

            return JsonManager.INSTANCE.mapper.readValue(stringCommandsJson, CommandsJson.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public void loadKitsIntoCache() {
        try(Connection connection = DatabaseManager.INSTANCE.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DatakitsStatements.LOAD_KIT_CACHE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String kitName = resultSet.getString("kit_name");
                String content = resultSet.getString("content");
                String command = resultSet.getString("commands");

                kitJSONCache.put(kitName, content);

                DataKits data = new DataKits();
                data.setName(kitName);
                data.setCommands(getCommands(command).getCommands());

                try {
                    data.setIcon(Serialization.INSTANCE.deserializeAndDecodeItemStack(JsonManager.INSTANCE.reader.readValue(content, DataKitsJson.class).getIcon()));
                    data.setItems(Serialization.INSTANCE.deserializeAndDecodeItemStackList(JsonManager.INSTANCE.reader.readValue(content, DataKitsJson.class).getItems()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                dataKits.put(kitName, data);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void saveKitsFromCache() {
        try(Connection connection = DatabaseManager.INSTANCE.getConnection()) {
            PreparedStatement insertStatement = connection.prepareStatement(DatakitsStatements.INSERT_KIT);
            PreparedStatement updateStatement = connection.prepareStatement(DatakitsStatements.UPDATE_KIT);
            PreparedStatement selectContentCommandsStatement = connection.prepareStatement(DatakitsStatements.SELECT_CONTENT_COMMANDS);
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

                String commands;

                try {
                    commands = JsonManager.INSTANCE.writer.writeValueAsString(datakit.getCommands());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (kitExistsAndChangedInDatabase(kitName, content, selectContentCommandsStatement, commands)) {
                    updateStatement.setString(1, content);
                    updateStatement.setString(2, commands);
                    updateStatement.setString(3, kitName);
                    updateStatement.executeUpdate();

                } else if (!kitExistsInDatabase(kitName, selectContentCommandsStatement)) {
                    insertStatement.setString(1, UUID.randomUUID().toString());
                    insertStatement.setString(2, kitName);
                    insertStatement.setString(3, content);
                    insertStatement.setString(4, commands);
                    insertStatement.executeUpdate();
                }
            }

            deleteKitsNotInCache(connection, deleteStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean kitExistsAndChangedInDatabase(String kitName, String newContent, PreparedStatement selectContentCommandsStatement, String newCommand) throws SQLException {
        selectContentCommandsStatement.setString(1, kitName);
        ResultSet resultSet = selectContentCommandsStatement.executeQuery();

        if (resultSet.next()) {
            String existingContent = resultSet.getString("content");
            String existingCommand = resultSet.getString("commands");
            return !existingContent.equals(newContent) || !existingCommand.equals(newCommand);
        }

        return false;
    }

    private boolean kitExistsInDatabase(String kitName, PreparedStatement selectContentCommandsStatement) throws SQLException {
        selectContentCommandsStatement.setString(1, kitName);
        ResultSet resultSet = selectContentCommandsStatement.executeQuery();
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

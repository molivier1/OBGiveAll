package mathano.mathano.utils;

import mathano.mathano.managers.DataKitsManager;
import mathano.mathano.managers.DatabaseManager;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AutoCompletion implements TabCompleter {
    List<String> arguments = new ArrayList<String>();

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> result = new ArrayList<String>();

        /*if (!((Player) sender).getPlayer().hasPermission("obgiveall.giveall")) {
            return result;
        }*/

        switch (command.getName()) {
            case "obgiveall":
                arguments.clear();
                if (args.length == 1) {
                    arguments.add("*");
                    Server server = sender.getServer();
                    Player [] players = server.getOnlinePlayers().toArray(new Player[server.getOnlinePlayers().size()]);
                    for (int i = 0; i < server.getOnlinePlayers().size(); i++) {
                        arguments.add(players[i].getName());
                    }
                }

                if (args.length == 2) {
                    //arguments.addAll(DataKitsManager.DATA_KITS_CONFIG.getKeys(false));
                    arguments.addAll(DatabaseManager.dataKits.keySet());
                }

                result = sendResult(args);
                break;
        }
        return result;
    }

    public List<String> sendResult(String[] args) {
        List<String> result = new ArrayList<String>();

        if (args.length == 1) {
            for (String a : arguments) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        } else if (args.length == 2) {
            for (String a : arguments) {
                if (a.toLowerCase().startsWith(args[1].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }
        return null;
    }
}

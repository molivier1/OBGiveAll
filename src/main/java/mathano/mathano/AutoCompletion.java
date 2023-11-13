package mathano.mathano;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class AutoCompletion implements TabCompleter {
    List<String> arguments = new ArrayList<String>();

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> result = new ArrayList<String>();

        switch (command.getName()) {
            /*case "kitsgui":
                if (arguments.isEmpty()) {

                }

                result = sendResult(args);
                break;*/

            case "obgiveall":
                if (arguments.isEmpty()) {
                    arguments.add("*");
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
        }
        return null;
    }
}

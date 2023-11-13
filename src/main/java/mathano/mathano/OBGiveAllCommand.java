package mathano.mathano;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class OBGiveAllCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equals("*")) {
                // Gives to everyone
            } else {
                // Gives to specific player
            }
        }
        return true;
    }
}

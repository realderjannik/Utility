package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PingCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      if (sender instanceof Player p) {
         FileConfiguration config = Utility.getInstance().getConfig();
         if (!config.getBoolean("use-command.ping", true)) {
            p.sendMessage(Colors.DISABLED);
            return true;
         } else if (cmd.getName().equalsIgnoreCase("ping")) {
            String var10001 = Colors.PREFIX;
            p.sendMessage(var10001 + Colors.YELLOW + "Your ping is: " + Colors.GREEN + p.getPing() + Colors.YELLOW + "ms");
            return true;
         } else {
            return false;
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}

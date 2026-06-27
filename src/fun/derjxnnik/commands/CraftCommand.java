package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CraftCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      if (sender instanceof Player p) {
         if (!sender.hasPermission("su.craftingtable")) {
            sender.sendMessage(Colors.NO_PERMS);
            return true;
         } else {
            FileConfiguration config = Utility.getInstance().getConfig();
            if (!config.getBoolean("use-command.craft", true)) {
               p.sendMessage(Colors.DISABLED);
               return true;
            } else {
               p.openWorkbench(p.getLocation(), true);
               return true;
            }
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}

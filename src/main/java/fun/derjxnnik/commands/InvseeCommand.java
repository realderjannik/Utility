package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Messages;
import fun.derjxnnik.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class InvseeCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      if (sender instanceof Player p) {
         FileConfiguration config = Utility.getInstance().getConfig();
         if (!config.getBoolean("use-command.invsee", true)) {
            p.sendMessage(Colors.DISABLED);
            return true;
         } else if (args.length != 1) {
            p.sendMessage(Messages.INVSEE_NUTZUNG);
            return true;
         } else {
            Player t = Bukkit.getPlayerExact(args[0]);
            if (t == null) {
               p.sendMessage(Messages.INVSEE_SPIELER_OFFLINE);
               return true;
            } else {
               Inventory inv = t.getInventory();
               p.openInventory(inv);
               return true;
            }
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}

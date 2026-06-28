package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class EnderChestCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      if (sender instanceof Player p) {
         FileConfiguration config = Utility.getInstance().getConfig();
         if (!config.getBoolean("use-command.ec", true)) {
            p.sendMessage(Colors.DISABLED);
            return true;
         } else if (args.length > 1) {
            p.sendMessage(Colors.PREFIX + Colors.RED + "Usage: /ec <player>");
            return true;
         } else if (args.length == 0) {
            Inventory pEnder = p.getEnderChest();
            p.openInventory(pEnder);
            return true;
         } else {
            Player t = Bukkit.getPlayerExact(args[0]);
            if (t == null) {
               p.sendMessage(Colors.PREFIX + Colors.RED + "Player is currently offline or has never played");
               return true;
            } else {
               Inventory tEnder = t.getEnderChest();
               p.openInventory(tEnder);
               return true;
            }
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}

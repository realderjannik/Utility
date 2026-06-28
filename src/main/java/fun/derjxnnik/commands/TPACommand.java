package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.tpa.TPAManager;
import fun.derjxnnik.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPACommand implements CommandExecutor {
   private final TPAManager tpaManager;

   public TPACommand(Utility plugin, TPAManager tpaManager) {
      this.tpaManager = tpaManager;
   }

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      if (sender instanceof Player player) {
         if (args.length != 1) {
            player.sendMessage(Colors.PREFIX + Colors.RED + "Usage: /tpa <player>");
            return true;
         } else {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
               player.sendMessage(Colors.PREFIX + Colors.RED + "Player not found or not online.");
               return true;
            } else {
               this.tpaManager.sendRequest(player, target);
               return true;
            }
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}

package fun.derjxnnik.tpa;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPAAcceptCommand implements CommandExecutor {
   private final TPAManager tpaManager;

   public TPAAcceptCommand(Utility plugin, TPAManager tpaManager) {
      this.tpaManager = tpaManager;
   }

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      if (sender instanceof Player player) {
         this.tpaManager.acceptRequest(player);
         return true;
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}

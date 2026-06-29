package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Messages;
import fun.derjxnnik.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HatCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      if (sender instanceof Player p) {
         if (!sender.hasPermission("utility.hat")) {
            sender.sendMessage(Colors.NO_PERMS);
            return true;
         } else {
            FileConfiguration config = Utility.getInstance().getConfig();
            if (!config.getBoolean("use-command.hat", true)) {
               p.sendMessage(Colors.DISABLED);
               return true;
            } else {
               ItemStack mainHand = p.getInventory().getItemInMainHand();
               ItemStack helmet = p.getInventory().getHelmet();
               if (mainHand.getType().isAir()) {
                  p.sendMessage(Messages.HAT_KEIN_ITEM);
                  return true;
               } else {
                  p.getInventory().setHelmet(mainHand);
                  p.getInventory().setItemInMainHand(helmet);
                  p.sendMessage(Messages.HAT_ANGEWENDET);
                  return true;
               }
            }
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}

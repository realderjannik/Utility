package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class RenameCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      if (sender instanceof Player p) {
         FileConfiguration config = Utility.getInstance().getConfig();
         if (!config.getBoolean("use-command.rename", true)) {
            p.sendMessage(Colors.DISABLED);
            return true;
         } else if (!p.hasPermission("utility.rename")) {
            p.sendMessage(Colors.NO_PERMS);
            return true;
         } else if (args.length == 0) {
            sender.sendMessage(Colors.PREFIX + Colors.RED + "Usage: /rename <Item_Name>");
            return true;
         } else {
            ItemStack active = p.getInventory().getItemInMainHand();
            if (active != null && !active.getType().isAir()) {
               String name = String.join(" ", args);
               name = ChatColor.translateAlternateColorCodes('&', name);
               ItemMeta meta = active.getItemMeta();
               if (meta != null) {
                  meta.setDisplayName(name);
                  active.setItemMeta(meta);
                  p.sendMessage(Colors.PREFIX + Colors.GREEN + "Renamed your item to: " + Colors.RESET + name);
                  return true;
               } else {
                  return true;
               }
            } else {
               sender.sendMessage(Colors.PREFIX + Colors.RED + "You must hold an item to rename it.");
               return true;
            }
         }
      } else {
         sender.sendMessage("Only players can use this command.");
         return true;
      }
   }
}

package fun.derjxnnik.commands;

import fun.derjxnnik.heads.HeadsCategoryGUI;
import fun.derjxnnik.heads.HeadsManager;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class HeadCommand implements CommandExecutor {
   private final HeadsManager headsManager;

   public HeadCommand(HeadsManager headsManager) {
      this.headsManager = headsManager;
   }

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
      if (sender instanceof Player p) {
         FileConfiguration config = Utility.getInstance().getConfig();
         if (!config.getBoolean("use-command.head", true)) {
            p.sendMessage(Colors.DISABLED);
            return true;
         } else if (args.length == 0) {
            (new HeadsCategoryGUI(this.headsManager)).open(p);
            return true;
         } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target.getName() == null) {
               p.sendMessage(Colors.PREFIX + Colors.RED + "This player has never joined before.");
               return true;
            } else {
               ItemStack head = new ItemStack(Material.PLAYER_HEAD);
               SkullMeta meta = (SkullMeta)head.getItemMeta();
               if (meta != null) {
                  meta.setOwningPlayer(target);
                  String var10001 = Colors.YELLOW;
                  meta.setDisplayName(var10001 + target.getName() + Colors.GRAY + "'s Head");
                  head.setItemMeta(meta);
               }

               p.getInventory().addItem(new ItemStack[]{head});
               String var10 = Colors.PREFIX;
               p.sendMessage(var10 + Colors.GREEN + "You received " + Colors.YELLOW + target.getName() + Colors.GRAY + "'s head!");
               return true;
            }
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}

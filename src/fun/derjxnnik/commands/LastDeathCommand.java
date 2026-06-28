package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LastDeathCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      if (sender instanceof Player p) {
         FileConfiguration config = Utility.getInstance().getConfig();
         if (!config.getBoolean("use-command.lastdeath", true)) {
            p.sendMessage(Colors.DISABLED);
            return true;
         } else {
            if (p.getLastDeathLocation() != null) {
               Location loc = p.getLastDeathLocation();
               int deathX = loc.getBlockX();
               int deathY = loc.getBlockY();
               int deathZ = loc.getBlockZ();
               String deathCause = "Unknown";
               if (p.getLastDamageCause() != null) {
                  p.getLastDamageCause();
                  deathCause = p.getLastDamageCause().getCause().toString().replace("_", " ").toLowerCase();
                  String var10000 = deathCause.substring(0, 1).toUpperCase();
                  deathCause = var10000 + deathCause.substring(1);
               }

               int timeInMinutes = p.getStatistic(Statistic.TIME_SINCE_DEATH) / 20 / 60;
               p.sendMessage(Colors.PREFIX + Colors.YELLOW + "Last " + Colors.RED + "death " + Colors.YELLOW + "information:\n" + Colors.GREEN + "Death cause: " + Colors.RED + deathCause + Colors.GREEN + "\nTime since: " + Colors.RED + timeInMinutes + " minutes" + Colors.GREEN + "\nX: " + Colors.RED + deathX + Colors.GREEN + "\nY: " + Colors.RED + deathY + Colors.GREEN + "\nZ: " + Colors.RED + deathZ);
            } else {
               String var10001 = Colors.PREFIX;
               p.sendMessage(var10001 + Colors.RED + p.getDisplayName() + " hasn't died yet.");
            }

            return true;
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}

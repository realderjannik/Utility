package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaytimeCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      if (sender instanceof Player p) {
         FileConfiguration config = Utility.getInstance().getConfig();
         if (!config.getBoolean("use-command.playtime", true)) {
            p.sendMessage(Colors.DISABLED);
            return true;
         } else if (args.length > 1) {
            p.sendMessage(Colors.PREFIX + Colors.RED + "Usage: /playtime <player>");
            return true;
         } else if (args.length == 0) {
            String var8 = Colors.PREFIX;
            p.sendMessage(var8 + Colors.YELLOW + "Your Playtime: " + Colors.GREEN + this.formatPlaytime(p));
            return true;
         } else {
            Player t = Bukkit.getPlayerExact(args[0]);
            if (t != null && t.isOnline()) {
               String var10001 = Colors.PREFIX;
               p.sendMessage(var10001 + Colors.YELLOW + t.getName() + "'s Playtime: " + Colors.GREEN + this.formatPlaytime(t));
               return true;
            } else {
               p.sendMessage(Colors.PREFIX + Colors.RED + "Player is currently offline or has never played");
               return true;
            }
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }

   public String formatPlaytime(Player p) {
      int totalMinutes = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;
      int days = totalMinutes / 1440;
      int hours = totalMinutes % 1440 / 60;
      int minutes = totalMinutes % 60;
      return days + " days, " + hours + " hours, " + minutes + " minutes";
   }
}

package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InfoCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      if (!(sender instanceof Player p)) {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }

      FileConfiguration config = Utility.getInstance().getConfig();
      if (!config.getBoolean("use-command.info", true)) {
         p.sendMessage(Colors.DISABLED);
         return true;
      }

      if (args.length != 1) {
         p.sendMessage(Colors.PREFIX + Colors.RED + "Usage: /info <player>");
         return true;
      }

      OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
      if (!t.hasPlayedBefore()) {
         p.sendMessage(Colors.PREFIX + Colors.RED + "Player has never played.");
         return true;
      }

      String status = t.isOnline() ? Colors.GREEN + "Online" : Colors.RED + "Offline";
      p.sendMessage(Colors.PREFIX + Colors.GRAY + "Info about " + Colors.WHITE + t.getName());
      p.sendMessage(Colors.GRAY + "Status: " + status);
      p.sendMessage(Colors.GRAY + "Playtime: " + playtime(t));
      return true;
   }

   private String playtime(OfflinePlayer player) {
      int totalMinutes = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;
      int days = totalMinutes / 1440;
      int hours = totalMinutes % 1440 / 60;
      int minutes = totalMinutes % 60;
      return Colors.YELLOW + days + "d, " + hours + "h, " + minutes + "m";
   }
}
